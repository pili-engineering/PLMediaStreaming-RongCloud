//
//  PushLiveViewController.m
//  NiuLiving
//
//  Created by 何昊宇 on 2018/3/16.
//  Copyright © 2018年 PILI. All rights reserved.
//

#import "PushLiveViewController.h"
#import <PLMediaStreamingKit/PLMediaStreamingKit.h>
#import "UIAlertView+BlocksKit.h"

@interface PushLiveViewController ()<PLMediaStreamingSessionDelegate>
@property (nonatomic, strong) PLMediaStreamingSession * session;
@property (nonatomic, strong) NSDictionary * settingDic;
@property (nonatomic, strong) NSURL * pushURL;
@end

@implementation PushLiveViewController

- (instancetype)initWithRoomName:(NSString *)roomName {
    if ([super init]) {
        _roomName = roomName;
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [self setupUI];
    if (![[NSUserDefaults standardUserDefaults] objectForKey:@"settingDic"]) {
        self.settingDic =@{@"isQuic":@(YES),
                           @"isAVFoundation":@(NO),
                           @"isVideoQualityPreinstall":@(YES),
                           @"isEncodePreinstall":@(YES),
                           @"isQualityFirst":@(YES),
                           @"isAdaptiveBitrate":@(YES),
                           @"isDebug":@(YES),
                           @"videoQualityPreinstall":@(5),
                           @"encodeSizePreinstall":@(1)};
    }else {
        self.settingDic = [[NSUserDefaults standardUserDefaults] objectForKey:@"settingDic"];
    }
    [self initPLSession];
}

- (void)initPLSession {
    if ([self.settingDic[@"isDebug"] boolValue]) {
        [PLStreamingEnv setLogLevel:PLStreamLogLevelDebug];
        [PLStreamingEnv enableFileLogging];
    }
    NSArray *encodeSize = @[@"240",@"424",@"480",@"848",@"544",@"960",@"720",@"1080",@"1088",@"1920"];
    PLVideoStreamingConfiguration *streamingConfig;
    if ([self.settingDic[@"isVideoQualityPreinstall"] boolValue]) {
        NSString * videoQuality = nil;
        switch ([self.settingDic[@"videoQualityPreinstall"] integerValue]) {
            case 0:
               videoQuality = kPLVideoStreamingQualityLow1;
                break;
            case 1:
                videoQuality = kPLVideoStreamingQualityLow2;
                break;
            case 2:
                videoQuality = kPLVideoStreamingQualityLow3;
                break;
            case 3:
                videoQuality = kPLVideoStreamingQualityMedium1;
                break;
            case 4:
                videoQuality = kPLVideoStreamingQualityMedium2;
                break;
            case 5:
                videoQuality = kPLVideoStreamingQualityMedium3;
                break;
            case 6:
                videoQuality = kPLVideoStreamingQualityHigh1;
                break;
            case 7:
                videoQuality = kPLVideoStreamingQualityHigh2;
                break;
            case 8:
                videoQuality = kPLVideoStreamingQualityHigh3;
                break;
                
            default:
                videoQuality = kPLVideoStreamingQualityMedium3;
                break;
        }
        streamingConfig = [PLVideoStreamingConfiguration configurationWithVideoQuality:videoQuality];
        streamingConfig.videoEncoderType = ![self.settingDic[@"isAVFoundation"] boolValue];
    }else {
        streamingConfig = [[PLVideoStreamingConfiguration alloc] initWithVideoSize:CGSizeMake(480, 848) expectedSourceVideoFrameRate:[self.settingDic[@"fps"] intValue]videoMaxKeyframeInterval:[self.settingDic[@"maxKeyframe"] intValue] averageVideoBitRate:([self.settingDic[@"bitrate"] floatValue] * 1024) videoProfileLevel:AVVideoProfileLevelH264HighAutoLevel videoEncoderType:![self.settingDic[@"isAVFoundation"] boolValue]];
    }
    
    if ([self.settingDic[@"isEncodePreinstall"] boolValue]) {
        int encodeSizeNumber= [self.settingDic[@"encodeSizePreinstall"] intValue];
        streamingConfig.videoSize = CGSizeMake( [[encodeSize objectAtIndex:encodeSizeNumber * 2] floatValue], [[encodeSize objectAtIndex:encodeSizeNumber * 2 + 1] floatValue]);
    }else {
        streamingConfig.videoSize = CGSizeMake([self.settingDic[@"width"] floatValue], [self.settingDic[@"height"] floatValue]);
    }
    self.session = [[PLMediaStreamingSession alloc] initWithVideoCaptureConfiguration:[PLVideoCaptureConfiguration defaultConfiguration] audioCaptureConfiguration:[PLAudioCaptureConfiguration defaultConfiguration] videoStreamingConfiguration:streamingConfig audioStreamingConfiguration:[PLAudioStreamingConfiguration defaultConfiguration] stream:nil];
    self.session.previewView.frame = [UIScreen mainScreen].bounds;
    self.session.delegate = self;
    [self.view insertSubview:self.session.previewView atIndex:0];
    [self.session setBeautifyModeOn:YES];
    [self.session setQuicEnable:[self.settingDic[@"isQuic"] boolValue]];
    if ([self.settingDic[@"isQuic"] boolValue]) {
        self.pushTypeLabel.text = @"推流协议：QUIC/RTMP";
    }else {
        self.pushTypeLabel.text = @"推流协议：TCP/RTMP";
    }
    self.beautyButton.selected = YES;
    [PLMediaStreamingSession requestMicrophoneAccessWithCompletionHandler:^(BOOL granted) {
        if (!granted) {
            [self showAlertWithMessage:@"获取麦克风权限失败,请去设置开启" completion:^{
                [self closeAction:nil];
            }];
        }
    }];
    [PLMediaStreamingSession requestCameraAccessWithCompletionHandler:^(BOOL granted) {
        if (granted) {
            if ([self.settingDic[@"isAdaptiveBitrate"] boolValue]) {
                [self.session enableAdaptiveBitrateControlWithMinVideoBitRate : 100*1024];
            }
            [self requestStreamURLWithCompleted:^(NSError *error, NSString *urlString) {
                if (urlString) {
                    self.pushURL = [NSURL URLWithString:urlString];
                    [self.session startStreamingWithPushURL:self.pushURL feedback:^(PLStreamStartStateFeedback feedback) {
                        if (feedback != PLStreamStartStateSuccess) {
                            [self showAlertWithMessage:@"推流失败，请重试" completion:^{
                                [self closeAction:nil];
                            }];
                        }
                    }];
                }else {
                    [self showAlertWithMessage:@"获取推流 URL 失败，请重试" completion:^{
                        [self closeAction:nil];
                    }];
                }
            }];
        }else {
            [self showAlertWithMessage:@"获取相机权限失败,请去设置开启" completion:^{
                [self closeAction:nil];
            }];
        }
    }];
    
}

- (void)setupUI {
    self.infoView.hidden = NO;
    self.messageView.alpha = 0;
    self.messageLabel.text = [NSString stringWithFormat:@"房间名： %@复制到剪贴板",self.roomName];
    
    [self.lightButton setImage:[UIImage imageNamed:@"light_on"] forState:UIControlStateNormal];
    [self.lightButton setImage:[UIImage imageNamed:@"light_off"] forState:UIControlStateSelected];
    self.lightButton.selected = NO;
}

#pragma mark - 请求数据

- (void)requestStreamURLWithCompleted:(void (^)(NSError *error, NSString *urlString))handler
{
    if ([[NSURL URLWithString:self.roomName].scheme isEqualToString:@"rtmp"]) {
        NSString *streamString = self.roomName;
        dispatch_async(dispatch_get_main_queue(), ^{
            handler(nil, streamString);
        });
        return;
    }
    
    NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"%@/v1/live/stream/%@",PLDomain, self.roomName]];
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:url];
    request.HTTPMethod = @"GET";
    request.timeoutInterval = 10;
    
    NSURLSessionDataTask *task = [[NSURLSession sharedSession] dataTaskWithRequest:request completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        if (error) {
            dispatch_async(dispatch_get_main_queue(), ^{
                handler(error, nil);
            });
            return;
        }
        
        NSString *streamString = [[NSString alloc] initWithData:data  encoding:NSUTF8StringEncoding];
        
        dispatch_async(dispatch_get_main_queue(), ^{
            handler(nil, streamString);
        });
        
    }];
    [task resume];
}

- (IBAction)infoAction:(id)sender {
    self.infoButton.selected = !self.infoButton.isSelected;
    if (!self.infoButton.isSelected) {
        self.infoView.hidden = NO;
    }else {
        self.infoView.hidden = YES;
    }
}
- (IBAction)urlCopyAction:(id)sender {
    UIPasteboard *pasteboard = [UIPasteboard generalPasteboard];
    pasteboard.string = self.roomName;
    [UIView animateWithDuration:1.0 animations:^{ // 执行动画
        self.messageView.alpha = 1.f;
    } completion:^(BOOL finished) { // 完成
        [UIView animateWithDuration:2.0 delay:1.0 options:UIViewAnimationOptionCurveLinear animations:^{
            self.messageView.alpha = 0;
        } completion:nil];
    }];
}
- (IBAction)closeAction:(id)sender {
    [self.session destroy];
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (IBAction)toggleCameraAction:(id)sender {
    self.toggleCameraButton.selected = !self.toggleCameraButton.isSelected;
    self.lightButton.hidden = self.toggleCameraButton.isSelected;
    [self.session toggleCamera];
        if (!self.toggleCameraButton.isSelected){
            if (self.lightButton.isSelected) { //打开闪光灯
                AVCaptureDevice *captureDevice = [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo];
                NSError *error = nil;
                
                if ([captureDevice hasTorch]) {
                    BOOL locked = [captureDevice lockForConfiguration:&error];
                    if (locked) {
                        captureDevice.torchMode = AVCaptureTorchModeOn;
                        [captureDevice unlockForConfiguration];
                    }
                }
            }else{//关闭闪光灯
                AVCaptureDevice *device = [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo];
                if ([device hasTorch]) {
                    [device lockForConfiguration:nil];
                    [device setTorchMode: AVCaptureTorchModeOff];
                    [device unlockForConfiguration];
                }
            }
        }
}

- (IBAction)lightAction:(id)sender {
    self.lightButton.selected = !self.lightButton.isSelected;
        if (!self.toggleCameraButton.isSelected){
            if (self.lightButton.isSelected) { //打开闪光灯
                AVCaptureDevice *captureDevice = [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo];
                NSError *error = nil;
                
                if ([captureDevice hasTorch]) {
                    BOOL locked = [captureDevice lockForConfiguration:&error];
                    if (locked) {
                        captureDevice.torchMode = AVCaptureTorchModeOn;
                        [captureDevice unlockForConfiguration];
                    }
                }
            }else{//关闭闪光灯
                AVCaptureDevice *device = [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo];
                if ([device hasTorch]) {
                    [device lockForConfiguration:nil];
                    [device setTorchMode: AVCaptureTorchModeOff];
                    [device unlockForConfiguration];
                }
            }
        }
}

- (IBAction)beautyAction:(id)sender {
    self.beautyButton.selected = !self.beautyButton.isSelected;
    [self.session setBeautify:self.beautyButton.isSelected];
    if (self.beautyButton.isSelected) {
        self.beautyButton.alpha = 1;
    }else {
        self.beautyButton.alpha = 0.4;
    }
}

- (void)showAlertWithMessage:(NSString *)message completion:(void (^)(void))completion
{
    if ([[[UIDevice currentDevice] systemVersion] floatValue] < 8.0) {
        UIAlertView *alertView = [UIAlertView bk_showAlertViewWithTitle:@"错误" message:message cancelButtonTitle:@"确定" otherButtonTitles:nil handler:^(UIAlertView *alertView, NSInteger buttonIndex) {
            if (completion) {
                completion();
            }
        }];
        [alertView show];
    }
    else {
        UIAlertController *controller = [UIAlertController alertControllerWithTitle:@"错误" message:message preferredStyle:UIAlertControllerStyleAlert];
        [controller addAction:[UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
            if (completion) {
                completion();
            }
        }]];
        [self presentViewController:controller animated:YES completion:nil];
    }
}

#pragma mark PLMediaStreamingSessionDelegate

- (void)mediaStreamingSession:(PLMediaStreamingSession *)session streamStatusDidUpdate:(PLStreamStatus *)status {
    self.resolutionLabel.text = [NSString stringWithFormat:@"分辨率：%.f * %.f",self.session.videoStreamingConfiguration.videoSize.width,self.session.videoStreamingConfiguration.videoSize.height];
    self.fpsLabel.text = [NSString stringWithFormat:@"视频帧率：%.2f ",status.videoFPS];
    self.audioFpsLabel.text = [NSString stringWithFormat:@"音频帧率：%.2f ",status.audioFPS];
    self.bitrateLabel.text = [NSString stringWithFormat:@"码率：%.2f kbps",status.totalBitrate/1000.0];
    
}

- (void)mediaStreamingSession:(PLMediaStreamingSession *)session streamStateDidChange:(PLStreamState)state {
    
}

- (void)mediaStreamingSession:(PLMediaStreamingSession *)session didDisconnectWithError:(NSError *)error {
    NSLog(@"error: %@", error);
    [self showAlertWithMessage:@"推流出错: 请重新推流" completion:^{
        [self closeAction:nil];
    }];
}



- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
