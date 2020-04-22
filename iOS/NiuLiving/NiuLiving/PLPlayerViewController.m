//
//  PLPlayerViewController.m
//  NiuLiving
//
//  Created by 何昊宇 on 2018/3/15.
//  Copyright © 2018年 PILI. All rights reserved.
//

#import "PLPlayerViewController.h"
#import <PLPlayerKit/PLPlayerKit.h>
#import "UIAlertView+BlocksKit.h"
#import "RCChatRoomView.h"
#import "RCChatroomWelcome.h"
#import "RCCRRongCloudIMManager.h"

#define SCREENSIZE [UIScreen mainScreen].bounds.size

@interface PLPlayerViewController ()<PLPlayerDelegate, UIGestureRecognizerDelegate>

@property (nonatomic, strong) PLPlayer * player;
@property (nonatomic, strong) NSTimer * timer;
@property (nonatomic, assign) NSInteger bufferingTime;
@property (nonatomic, strong) RCChatRoomView * chatRoomView;
@property (nonatomic, strong) RCCRLiveModel * model;

@end

@implementation PLPlayerViewController

- (instancetype)initWithRoomName:(NSString *)roomName model:(RCCRLiveModel *)model{
    if ([super init]) {
        _roomName = roomName;
        _model = model;
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    self.bufferingTime = 0;
    self.timer = [NSTimer scheduledTimerWithTimeInterval:1.0f target:self selector:@selector(playerInfo) userInfo:nil repeats:YES];
    [self setupUI];
    [self requestPlayUrlWithCompleted:^(NSError *error, NSString *playUrl) {
        if (!playUrl) {
            [self showAlertWithMessage:@"网络故障，未获取到播放链接" completion:^{
                [self closeAction:nil];
            } ];
            
        }else {
            [self setupPlayer:playUrl];
            [self joinChatRoom];
        }
    }];
}

- (void)resetBottomGesture:(UIGestureRecognizer *)gestureRecognizer {
    if (gestureRecognizer.state == UIGestureRecognizerStateEnded) {
        [self.chatRoomView setDefaultBottomViewStatus];
    }
}

/**
 拦截加在整个背景view上的点击手势
 
 @param gestureRecognizer UIGestureRecognizer
 @param touch UITouch
 @return BOOL
 */
-(BOOL)gestureRecognizer:(UIGestureRecognizer *)gestureRecognizer shouldReceiveTouch:(UITouch *)touch{
    if ([touch.view isDescendantOfView:self.chatRoomView.bottomBtnContentView] || [touch.view isDescendantOfView:self.chatRoomView.giftListView]) {
        return NO;
    }
    return YES;
}

- (void)setupUI {
    self.infoView.hidden = YES;
//    self.messageView.hidden = YES;
    self.messageView.alpha = 0;
    self.messageLabel.text = [NSString stringWithFormat:@"房间名： %@复制到剪贴板",self.roomName];
    self.reportButton.layer.cornerRadius = 10.0;
    CGFloat bottomExtraDistance  = 0;
    if (@available(iOS 11.0, *)) {
        bottomExtraDistance = [self getIPhonexExtraBottomHeight];
    }
    self.chatRoomView = [[RCChatRoomView alloc] initWithFrame:CGRectMake(0,SCREENSIZE.height - (237 +50)  - bottomExtraDistance,SCREENSIZE.width, 237+50) model:self.model];
    [self.view addSubview:self.chatRoomView];
    UITapGestureRecognizer *resetBottomTapGesture =[[UITapGestureRecognizer alloc]
                                   initWithTarget:self
                                   action:@selector(resetBottomGesture:)];
          resetBottomTapGesture.delegate = self;
          [self.view addGestureRecognizer:resetBottomTapGesture];
}

- (void)joinChatRoom {
    [[RCIMClient sharedRCIMClient] joinChatRoom:self.model.roomId messageCount:-1 success:^{
        RCChatroomWelcome *joinChatroomMessage = [[RCChatroomWelcome alloc]init];
                [joinChatroomMessage setId:[RCCRRongCloudIMManager sharedRCCRRongCloudIMManager].currentUserInfo.userId];
                [self.chatRoomView sendMessage:joinChatroomMessage pushContent:nil success:nil error:nil];
    } error:^(RCErrorCode status) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.chatRoomView alertErrorWithTitle:@"提示" message:@"加入聊天室失败" ok:@"知道了"];
        });
    }];
}

- (void)playerInfo {
    self.resolutionLabel.text = [NSString stringWithFormat:@"分辨率： %d * %d",self.player.width,self.player.height];
    self.fpsLabel.text = [NSString stringWithFormat:@"视频帧率： %d",self.player.videoFPS];
    self.bitrateLabel.text = [NSString stringWithFormat:@"视频码率： %.2f kb/s",self.player.bitrate];
}

- (void)requestPlayUrlWithCompleted:(void (^)(NSError *error, NSString *playUrl))handler
{
    if ([[NSURL URLWithString:self.roomName].scheme isEqualToString:@"rtmp"] ||
        [[NSURL URLWithString:self.roomName].scheme isEqualToString:@"http"] ||
        [[NSURL URLWithString:self.roomName].scheme isEqualToString:@"https"]) {
        NSString *url = self.roomName;
        dispatch_async(dispatch_get_main_queue(), ^{
            handler(nil, url);
        });
        return;
    }

    NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"%@/v1/live/play/%@/rtmp",PLDomain, self.roomName]];
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
        
        NSString *url = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
        dispatch_async(dispatch_get_main_queue(), ^{
            handler(nil, url);
        });
    }];
    [task resume];
}

- (void)setupPlayer:(NSString *)playURL {
    self.player = [PLPlayer playerWithURL:[NSURL URLWithString:playURL] option:[PLPlayerOption defaultOption]];
    self.player.playerView.contentMode = UIViewContentModeScaleAspectFit;
    self.player.playerView.frame = [UIScreen mainScreen].bounds;
    [self.view insertSubview:self.player.playerView atIndex:0];
    self.player.delegate = self;
    self.player.backgroundPlayEnable = YES;
    [self.player play];
}

- (IBAction)infoAction:(id)sender {
    self.infoButton.selected = !self.infoButton.isSelected;
    if (self.infoButton.isSelected) {
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
    [self.timer invalidate];
    self.timer = nil;
    [self.player.playerView removeFromSuperview];
    self.player.delegate = nil;
    self.player = nil;
    [self.navigationController popViewControllerAnimated:NO];
//    [self dismissViewControllerAnimated:YES completion:nil];
}
- (IBAction)reportAction:(id)sender {
    [self showAlertWithMessage:@"您已举报此房间，我们会尽快处理" completion:^{
        [self closeAction:nil];
    }];
}

#pragma mark PLPlayerDelegate

- (void)player:(PLPlayer *)player statusDidChange:(PLPlayerStatus)state {
    if (state == PLPlayerStatusCaching) {
        self.bufferingLabel.text = [NSString stringWithFormat:@"卡顿次数：%ld 次",self.bufferingTime + 1];
    }
}

- (void)player:(PLPlayer *)player stoppedWithError:(NSError *)error {
    NSLog(@"error: %@", error);
    [self showAlertWithMessage:@"播放出错: 请检查是否还在推流中" completion:^{
        [self closeAction:nil];
    }];
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

- (float)getIPhonexExtraBottomHeight {
    float height = 0;
    if (@available(iOS 11.0, *)) {
        height = [[[UIApplication sharedApplication] keyWindow] safeAreaInsets].bottom;
    }
    return height;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


@end
