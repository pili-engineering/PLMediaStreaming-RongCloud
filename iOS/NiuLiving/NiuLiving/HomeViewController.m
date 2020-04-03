//
//  HomeViewController.m
//  NiuLiving
//
//  Created by 何昊宇 on 2018/3/12.
//  Copyright © 2018年 PILI. All rights reserved.
//

#import "HomeViewController.h"
#import "SettingViewController.h"
#import "CreateRoomViewController.h"
#import "GoingRoomViewController.h"
#import "UIAlertView+BlocksKit.h"

@interface HomeViewController ()

@end

@implementation HomeViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    if ([[[NSBundle mainBundle] bundleIdentifier] isEqualToString:BundleIdentifierInFir]) {
        //企业版
        [self requestUpgradeURLWithCompleted:^(NSError *error, NSDictionary *upgradeDic) {
            if ([[upgradeDic objectForKey:@"Version"] integerValue] > PLUpgrade) {
                [self showAlertWithMessage:@"有新版本更新" completion:^{
                    dispatch_async(dispatch_get_main_queue(), ^{
                        [[UIApplication sharedApplication] openURL:[NSURL URLWithString:FirLink]];
                    });
                }];
            }
        }];
    }
    
    [self setupUI];
}

- (void)viewWillAppear:(BOOL)animated {
    self.navigationController.navigationBar.hidden = YES;
}
- (void)setupUI {
    
    CAGradientLayer *pushButtonlayer = [[CAGradientLayer alloc] init];
    pushButtonlayer.frame = self.pushButton.bounds;
    pushButtonlayer.colors =  [NSArray arrayWithObjects:
                               (id) [[UIColor colorWithRed:14.0f / 255.0f green:183.0f / 255.0f blue:255.0f / 255.0f alpha:1] CGColor],
                               (id) [[UIColor colorWithRed:6.0f / 255.0f green:129.0f / 255.0f blue:255.0f / 255.0f alpha:1] CGColor],
                               nil];
    pushButtonlayer.startPoint = CGPointMake(0, 0);
    pushButtonlayer.endPoint = CGPointMake(1, 0);
    [self.pushButton.layer insertSublayer:pushButtonlayer atIndex:0];
    self.pushButton.layer.cornerRadius = 20.0;
    pushButtonlayer.cornerRadius = 20.0;
    
    
    CAGradientLayer *playButtonlayer = [[CAGradientLayer alloc] init];
    playButtonlayer.frame = self.playButton.bounds;
    playButtonlayer.colors =  [NSArray arrayWithObjects:
                               (id) [[UIColor colorWithRed:14.0f / 255.0f green:183.0f / 255.0f blue:255.0f / 255.0f alpha:1] CGColor],
                               (id) [[UIColor colorWithRed:6.0f / 255.0f green:129.0f / 255.0f blue:255.0f / 255.0f alpha:1] CGColor],
                               nil];
    playButtonlayer.startPoint = CGPointMake(0, 0);
    playButtonlayer.endPoint = CGPointMake(1, 0);
    [self.playButton.layer insertSublayer:playButtonlayer atIndex:0];
    self.playButton.layer.cornerRadius = 20.0;
    playButtonlayer.cornerRadius = 20.0;
}

- (void)requestUpgradeURLWithCompleted:(void (^)(NSError *error, NSDictionary *upgradeDic))handler
{
    NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"%@/v1/upgrade/app?appId=com.qiniu.QiNiuLiving",PLDomain]];
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
        
        NSDictionary *upgradeDic = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableLeaves error:&error];
        
        dispatch_async(dispatch_get_main_queue(), ^{
            handler(nil, upgradeDic);
        });
        
    }];
    [task resume];
}

- (void)showAlertWithMessage:(NSString *)message completion:(void (^)(void))completion
{
    if ([[[UIDevice currentDevice] systemVersion] floatValue] < 8.0) {
        UIAlertView *alertView = [UIAlertView bk_showAlertViewWithTitle:@"版本更新" message:message cancelButtonTitle:@"更新" otherButtonTitles:@[@"取消"] handler:^(UIAlertView *alertView, NSInteger buttonIndex) {
            if (buttonIndex == 0) {
                if (completion) {
                    completion();
                }
            }
            
        }];
        [alertView show];
    }
    else {
        UIAlertController *controller = [UIAlertController alertControllerWithTitle:@"版本更新" message:message preferredStyle:UIAlertControllerStyleAlert];
        [controller addAction:[UIAlertAction actionWithTitle:@"更新" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            if (completion) {
                completion();
            }
        }]];
        [controller addAction:[UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
        }]];
        [self presentViewController:controller animated:YES completion:nil];
    }
}

- (IBAction)pushAction:(id)sender {
    CreateRoomViewController * createRoomVC = [[CreateRoomViewController alloc] init];
    [self.navigationController pushViewController:createRoomVC
                                         animated:YES];
}
- (IBAction)playAction:(id)sender {
    GoingRoomViewController * goingRoomVC = [[GoingRoomViewController alloc] init];
    [self.navigationController pushViewController:goingRoomVC animated:YES];
}
- (IBAction)settingAction:(id)sender {
    SettingViewController * settingVC = [[SettingViewController alloc] init];
    [self.navigationController pushViewController:settingVC animated:YES];
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
