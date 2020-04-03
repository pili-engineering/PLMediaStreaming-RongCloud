//
//  SettingViewController.h
//  NiuLiving
//
//  Created by 何昊宇 on 2018/3/12.
//  Copyright © 2018年 PILI. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "LMJDropdownMenu.h"

@interface SettingViewController : UIViewController
@property (weak, nonatomic) IBOutlet UIButton *quicButton;
@property (weak, nonatomic) IBOutlet UIButton *tcpButton;
@property (weak, nonatomic) IBOutlet UIButton *avFoundationButton;
@property (weak, nonatomic) IBOutlet UIButton *videoToolboxButton;
@property (weak, nonatomic) IBOutlet UIButton *videoQualityPreinstallButton;
@property (weak, nonatomic) IBOutlet UIButton *videoQualityCustomButton;
@property (weak, nonatomic) IBOutlet UIButton *encodePreinstallButton;
@property (weak, nonatomic) IBOutlet UIButton *encodeCustomButton;
@property (weak, nonatomic) IBOutlet UIButton *qualityFirstButton;
@property (weak, nonatomic) IBOutlet UIButton *bitrateFirstButton;
@property (weak, nonatomic) IBOutlet UIButton *AdaptiveBitrateOpenButton;
@property (weak, nonatomic) IBOutlet UIButton *AdaptiveBitrateCloseButton;
@property (weak, nonatomic) IBOutlet UIButton *openDebugButton;
@property (weak, nonatomic) IBOutlet UIButton *closeDebugButton;

@property (weak, nonatomic) IBOutlet UIView *videoQualityCustomView;
@property (weak, nonatomic) IBOutlet UIView *encodeSizeCustomView;
@property (weak, nonatomic) IBOutlet LMJDropdownMenu *videoQualityView;
@property (weak, nonatomic) IBOutlet LMJDropdownMenu *encodeSizeView;
@property (weak, nonatomic) IBOutlet UITextField *fpsTextField;
@property (weak, nonatomic) IBOutlet UITextField *bitrateTextField;
@property (weak, nonatomic) IBOutlet UITextField *maxKeyframeTextField;
@property (weak, nonatomic) IBOutlet UITextField *widthTextField;
@property (weak, nonatomic) IBOutlet UITextField *heightTextField;

@property (nonatomic, strong) NSMutableDictionary * settingDic;

@end
