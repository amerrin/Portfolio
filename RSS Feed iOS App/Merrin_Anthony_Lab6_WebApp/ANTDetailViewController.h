//
//  ANTDetailViewController.h
//  RSS Reader iOS App
//
//  Created by Anthony Merrin on 11/20/13.
//  Copyright (c) 2013 Anthony Merrin. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ANTDetailViewController : UIViewController <UISplitViewControllerDelegate>
- (IBAction)postToFacebook:(id)sender;

@property (strong, nonatomic) id detailItem;
@property (strong, nonatomic) id detailDescription;

@end
