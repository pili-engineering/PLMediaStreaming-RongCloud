//
//  PLPlayerViewController.h
//  NiuLiving
//
//  Created by 何昊宇 on 2018/3/15.
//  Copyright © 2018年 PILI. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface PLPlayerViewController : UIViewController
@property (weak, nonatomic) IBOutlet UIButton *infoButton;
@property (weak, nonatomic) IBOutlet UIButton *urlCopyButton;
@property (weak, nonatomic) IBOutlet UIButton *closeButton;
@property (weak, nonatomic) IBOutlet UIView *messageView;
@property (weak, nonatomic) IBOutlet UILabel *messageLabel;
@property (weak, nonatomic) IBOutlet UIView *infoView;
@property (weak, nonatomic) IBOutlet UILabel *bufferingLabel;
@property (weak, nonatomic) IBOutlet UILabel *resolutionLabel;
@property (weak, nonatomic) IBOutlet UILabel *fpsLabel;
@property (weak, nonatomic) IBOutlet UILabel *bitrateLabel;
@property (weak, nonatomic) IBOutlet UIButton *reportButton;

@property (nonatomic, strong) NSString * roomName;

- (instancetype)initWithRoomName:(NSString *)roomName;

@end
