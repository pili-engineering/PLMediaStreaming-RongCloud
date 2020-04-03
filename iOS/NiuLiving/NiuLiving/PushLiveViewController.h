//
//  PushLiveViewController.h
//  NiuLiving
//
//  Created by 何昊宇 on 2018/3/16.
//  Copyright © 2018年 PILI. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface PushLiveViewController : UIViewController
@property (weak, nonatomic) IBOutlet UIButton *infoButton;
@property (weak, nonatomic) IBOutlet UIButton *urlCopyButton;
@property (weak, nonatomic) IBOutlet UIButton *closeButton;
@property (weak, nonatomic) IBOutlet UIButton *toggleCameraButton;
@property (weak, nonatomic) IBOutlet UIButton *lightButton;
@property (weak, nonatomic) IBOutlet UIButton *beautyButton;
@property (weak, nonatomic) IBOutlet UIView *messageView;
@property (weak, nonatomic) IBOutlet UILabel *messageLabel;
@property (weak, nonatomic) IBOutlet UIView *infoView;
@property (weak, nonatomic) IBOutlet UILabel *pushTypeLabel;
@property (weak, nonatomic) IBOutlet UILabel *resolutionLabel;
@property (weak, nonatomic) IBOutlet UILabel *fpsLabel;
@property (weak, nonatomic) IBOutlet UILabel *bitrateLabel;
@property (weak, nonatomic) IBOutlet UILabel *audioBitrateLabel;
@property (weak, nonatomic) IBOutlet UILabel *audioFpsLabel;

@property (nonatomic, strong) NSString *roomName;

- (instancetype)initWithRoomName:(NSString *)roomName;

@end
