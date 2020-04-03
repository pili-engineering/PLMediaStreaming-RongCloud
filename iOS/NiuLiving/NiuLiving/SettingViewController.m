//
//  SettingViewController.m
//  NiuLiving
//
//  Created by 何昊宇 on 2018/3/12.
//  Copyright © 2018年 PILI. All rights reserved.
//

#import "SettingViewController.h"
#import "UIScrollView+UITouchEvent.h"
#import <PLMediaStreamingKit/PLMediaStreamingKit.h>

@interface SettingViewController()<LMJDropdownMenuDelegate>

@end

@implementation SettingViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    if ([[NSUserDefaults standardUserDefaults] objectForKey:@"settingDic"] == nil)  {
        self.settingDic = [[NSMutableDictionary alloc] initWithDictionary:@{@"isQuic":@(YES),
                                 @"isAVFoundation":@(NO),
                                 @"isVideoQualityPreinstall":@(YES),
                                 @"isEncodePreinstall":@(YES),
                                 @"isQualityFirst":@(YES),
                                 @"isAdaptiveBitrate":@(YES),
                                 @"isDebug":@(YES),
                                 @"videoQualityPreinstall":@(5),
                                 @"encodeSizePreinstall":@(1),
                                 @"fps":@(24),
                                 @"bitrate":@(1000),
                                 @"maxKeyframe":@(72),
                                 @"width":@(480),
                                 @"height":@(848)}];
    }else {
        self.settingDic = [[NSMutableDictionary alloc] initWithDictionary:[[NSUserDefaults standardUserDefaults] objectForKey:@"settingDic"]];
    }
    [self setupUI];
}

- (void)viewWillAppear:(BOOL)animated {
    self.navigationController.navigationBar.hidden = NO;
    self.title = @"直播高级设置";
    UIBarButtonItem *backItem = [[UIBarButtonItem alloc] initWithTitle:@"Done" style:UIBarButtonItemStyleDone target:self action:@selector(backAction)];
    self.navigationItem.leftBarButtonItem = backItem;
}

- (void)backAction {
    [self.settingDic setObject:@(self.videoQualityView.mainBtn.tag) forKey:@"videoQualityPreinstall"];
    [self.settingDic setObject:self.fpsTextField.text forKey:@"fps"];
    [self.settingDic setObject:self.bitrateTextField.text forKey:@"bitrate"];
    [self.settingDic setObject:self.maxKeyframeTextField.text forKey:@"maxKeyframe"];
    
    [self.settingDic setObject:@(self.encodeSizeView.mainBtn.tag) forKey:@"encodeSizePreinstall"];
    [self.settingDic setObject:self.widthTextField.text forKey:@"width"];
    [self.settingDic setObject:self.heightTextField.text forKey:@"height"];
    NSDictionary * userSettingDic = [NSDictionary dictionaryWithDictionary:self.settingDic];
    [[NSUserDefaults standardUserDefaults] setObject:userSettingDic forKey:@"settingDic"];
    [self.navigationController popViewControllerAnimated:YES];
}

- (void)setupUI {
    self.fpsTextField.text = [NSString stringWithFormat:@"%d",[self.settingDic[@"fps"] intValue]];
    self.bitrateTextField.text = [NSString stringWithFormat:@"%d",[self.settingDic[@"bitrate"] intValue]];
    self.maxKeyframeTextField.text = [NSString stringWithFormat:@"%d",[self.settingDic[@"maxKeyframe"] intValue]];
    self.widthTextField.text = [NSString stringWithFormat:@"%d",[self.settingDic[@"width"] intValue]];
    self.heightTextField.text = [NSString stringWithFormat:@"%d",[self.settingDic[@"height"] intValue]];
    
    [self.quicButton setImage:[UIImage imageNamed:@"chose.png"] forState:UIControlStateSelected];
    [self.quicButton setImage:[UIImage imageNamed:@"noChose.png"] forState:UIControlStateNormal];
    self.quicButton.selected = [self.settingDic[@"isQuic"] boolValue];
    
    [self.tcpButton setImage:[UIImage imageNamed:@"chose.png"] forState:UIControlStateSelected];
    [self.tcpButton setImage:[UIImage imageNamed:@"noChose.png"] forState:UIControlStateNormal];
    self.tcpButton.selected = ![self.settingDic[@"isQuic"] boolValue];
    
    [self.avFoundationButton setImage:[UIImage imageNamed:@"chose.png"] forState:UIControlStateSelected];
    [self.avFoundationButton setImage:[UIImage imageNamed:@"noChose.png"] forState:UIControlStateNormal];
    self.avFoundationButton.selected = [self.settingDic[@"isAVFoundation"] boolValue];
    
    [self.videoToolboxButton setImage:[UIImage imageNamed:@"chose.png"] forState:UIControlStateSelected];
    [self.videoToolboxButton setImage:[UIImage imageNamed:@"noChose.png"] forState:UIControlStateNormal];
    self.videoToolboxButton.selected = ![self.settingDic[@"isAVFoundation"] boolValue];
    
    [self.videoQualityPreinstallButton setImage:[UIImage imageNamed:@"chose.png"] forState:UIControlStateSelected];
    [self.videoQualityPreinstallButton setImage:[UIImage imageNamed:@"noChose.png"] forState:UIControlStateNormal];
    self.videoQualityPreinstallButton.selected = [self.settingDic[@"isVideoQualityPreinstall"] boolValue];
    self.videoQualityView.hidden = ![self.settingDic[@"isVideoQualityPreinstall"] boolValue];
    
    [self.videoQualityCustomButton setImage:[UIImage imageNamed:@"chose.png"] forState:UIControlStateSelected];
    [self.videoQualityCustomButton setImage:[UIImage imageNamed:@"noChose.png"] forState:UIControlStateNormal];
    self.videoQualityCustomButton.selected = ![self.settingDic[@"isVideoQualityPreinstall"] boolValue];
    self.videoQualityCustomView.hidden = [self.settingDic[@"isVideoQualityPreinstall"] boolValue];
    
    [self.encodePreinstallButton setImage:[UIImage imageNamed:@"chose.png"] forState:UIControlStateSelected];
    [self.encodePreinstallButton setImage:[UIImage imageNamed:@"noChose.png"] forState:UIControlStateNormal];
    self.encodePreinstallButton.selected = [self.settingDic[@"isEncodePreinstall"] boolValue];
    self.encodeSizeView.hidden = ![self.settingDic[@"isEncodePreinstall"] boolValue];
    
    [self.encodeCustomButton setImage:[UIImage imageNamed:@"chose.png"] forState:UIControlStateSelected];
    [self.encodeCustomButton setImage:[UIImage imageNamed:@"noChose.png"] forState:UIControlStateNormal];
    self.encodeCustomButton.selected = ![self.settingDic[@"isEncodePreinstall"] boolValue];
    self.encodeSizeCustomView.hidden = [self.settingDic[@"isEncodePreinstall"] boolValue];
    
    [self.qualityFirstButton setImage:[UIImage imageNamed:@"chose.png"] forState:UIControlStateSelected];
    [self.qualityFirstButton setImage:[UIImage imageNamed:@"noChose.png"] forState:UIControlStateNormal];
    self.qualityFirstButton.selected = YES;
    
    [self.bitrateFirstButton setImage:[UIImage imageNamed:@"chose.png"] forState:UIControlStateSelected];
    [self.bitrateFirstButton setImage:[UIImage imageNamed:@"noChose.png"] forState:UIControlStateNormal];
    self.bitrateFirstButton.selected = NO;
    
    [self.AdaptiveBitrateOpenButton setImage:[UIImage imageNamed:@"chose.png"] forState:UIControlStateSelected];
    [self.AdaptiveBitrateOpenButton setImage:[UIImage imageNamed:@"noChose.png"] forState:UIControlStateNormal];
    self.AdaptiveBitrateOpenButton.selected = [self.settingDic[@"isAdaptiveBitrate"] boolValue];
    
    [self.AdaptiveBitrateCloseButton setImage:[UIImage imageNamed:@"chose.png"] forState:UIControlStateSelected];
    [self.AdaptiveBitrateCloseButton setImage:[UIImage imageNamed:@"noChose.png"] forState:UIControlStateNormal];
    self.AdaptiveBitrateCloseButton.selected = ![self.settingDic[@"isAdaptiveBitrate"] boolValue];
    
    [self.openDebugButton setImage:[UIImage imageNamed:@"chose.png"] forState:UIControlStateSelected];
    [self.openDebugButton setImage:[UIImage imageNamed:@"noChose.png"] forState:UIControlStateNormal];
    self.openDebugButton.selected = [self.settingDic[@"isDebug"] boolValue];
    
    [self.closeDebugButton setImage:[UIImage imageNamed:@"chose.png"] forState:UIControlStateSelected];
    [self.closeDebugButton setImage:[UIImage imageNamed:@"noChose.png"] forState:UIControlStateNormal];
    self.closeDebugButton.selected = ![self.settingDic[@"isDebug"] boolValue];
    
    NSArray * videoQualityArray = @[@"kPLVideoStreamingQualityLow1",@"kPLVideoStreamingQualityLow2",@"kPLVideoStreamingQualityLow3",@"kPLVideoStreamingQualityMedium1",@"kPLVideoStreamingQualityMedium2",@"kPLVideoStreamingQualityMedium3",@"kPLVideoStreamingQualityHigh1",@"kPLVideoStreamingQualityHigh2",@"kPLVideoStreamingQualityHigh3"];
    [self.videoQualityView setMenuTitles:videoQualityArray rowHeight:40];
    self.videoQualityView.mainBtn.tag = [self.settingDic[@"videoQualityPreinstall"] integerValue];
    self.videoQualityView.delegate = self;
    [self.videoQualityView.mainBtn setTitle:videoQualityArray[self.videoQualityView.mainBtn.tag] forState:UIControlStateNormal];
    
    NSArray * encodeSizeArray = @[@"240P【424x240(16:9)】",@"480P【848x480(16:9)】",@"544P【960x544(16:9)】",@"720P【1280x720(16:9)】",@"1088P【1920x1088(16:9)】"];
    
    [self.encodeSizeView setMenuTitles:encodeSizeArray rowHeight:40];
    self.encodeSizeView.mainBtn.tag = [self.settingDic[@"encodeSizePreinstall"] intValue];
    self.encodeSizeView.delegate = self;
    [self.encodeSizeView.mainBtn setTitle:encodeSizeArray[self.encodeSizeView.mainBtn.tag] forState:UIControlStateNormal];
    
}

- (IBAction)quicAction:(id)sender {
    self.quicButton.selected = YES;
    self.tcpButton.selected = NO;
    [self.settingDic setObject:@(YES) forKey:@"isQuic"];
}

- (IBAction)tcpAction:(id)sender {
    self.quicButton.selected = NO;
    self.tcpButton.selected = YES;
    [self.settingDic setObject:@(NO) forKey:@"isQuic"];
}

- (IBAction)avFoundationAction:(id)sender {
    self.avFoundationButton.selected = YES;
    self.videoToolboxButton.selected = NO;
    [self.settingDic setObject:@(YES) forKey:@"isAVFoundation"];
}

- (IBAction)videoToolboxAction:(id)sender {
    self.avFoundationButton.selected = NO;
    self.videoToolboxButton.selected = YES;
    [self.settingDic setObject:@(NO) forKey:@"isAVFoundation"];
}

- (IBAction)videoQualityPreinstallAction:(id)sender {
    self.videoQualityView.hidden = NO;
    self.videoQualityCustomView.hidden = YES;
    self.videoQualityPreinstallButton.selected = YES;
    self.videoQualityCustomButton.selected = NO;
    [self.settingDic setObject:@(YES) forKey:@"isVideoQualityPreinstall"];
}

- (IBAction)videoQualityCustomAction:(id)sender {
    self.videoQualityView.hidden = YES;
    [self.videoQualityView hideDropDown];
    self.videoQualityCustomView.hidden = NO;
    self.videoQualityPreinstallButton.selected = NO;
    self.videoQualityCustomButton.selected = YES;
    [self.settingDic setObject:@(NO) forKey:@"isVideoQualityPreinstall"];
}

- (IBAction)encodePreinstallAction:(id)sender {
    self.encodeSizeView.hidden = NO;
    self.encodeSizeCustomView.hidden = YES;
    self.encodePreinstallButton.selected = YES;
    self.encodeCustomButton.selected = NO;
    [self.settingDic setObject:@(YES) forKey:@"isEncodePreinstall"];
}

- (IBAction)encodeCustomAction:(id)sender {
    [self.encodeSizeView hideDropDown];
    self.encodeSizeView.hidden = YES;
    self.encodeSizeCustomView.hidden = NO;
    self.encodePreinstallButton.selected = NO;
    self.encodeCustomButton.selected = YES;
    [self.settingDic setObject:@(NO) forKey:@"isEncodePreinstall"];
}

- (IBAction)qualityFirstAction:(id)sender {
    self.qualityFirstButton.selected = YES;
    self.bitrateFirstButton.selected = NO;
    [self.settingDic setObject:@(YES) forKey:@"isQualityFirst"];
}

- (IBAction)bitrateFirstAction:(id)sender {
    self.qualityFirstButton.selected = NO;
    self.bitrateFirstButton.selected = YES;
    [self.settingDic setObject:@(NO) forKey:@"isQualityFirst"];
}

- (IBAction)AdaptiveBitrateOpenAction:(id)sender {
    self.AdaptiveBitrateOpenButton.selected = YES;
    self.AdaptiveBitrateCloseButton.selected = NO;
    [self.settingDic setObject:@(YES) forKey:@"isAdaptiveBitrate"];
}

- (IBAction)AdaptiveBitrateCloseAction:(id)sender {
    self.AdaptiveBitrateOpenButton.selected = NO;
    self.AdaptiveBitrateCloseButton.selected = YES;
     [self.settingDic setObject:@(NO) forKey:@"isAdaptiveBitrate"];
}

- (IBAction)openDebugAction:(id)sender {
    self.openDebugButton.selected = YES;
    self.closeDebugButton.selected = NO;
    [self.settingDic setObject:@(YES) forKey:@"isDebug"];
}

- (IBAction)closeDebugAction:(id)sender {
    self.openDebugButton.selected = NO;
    self.closeDebugButton.selected = YES;
    [self.settingDic setObject:@(NO) forKey:@"isDebug"];
}

- (void)dropdownMenu:(LMJDropdownMenu *)menu selectedCellNumber:(NSInteger)number {
    if (menu == self.encodeSizeView) {
        self.encodeSizeView.mainBtn.tag = number;
    }
    if (menu == self.videoQualityView) {
        self.videoQualityView.mainBtn.tag = number;
    }
    
    
}

- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
     [self.view endEditing:YES];
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
