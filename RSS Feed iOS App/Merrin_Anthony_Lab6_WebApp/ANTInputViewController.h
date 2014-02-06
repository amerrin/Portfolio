//
//  ANTViewController.h
//  InputText
//
//  Created by Anthony Merrin on 10/20/13.
//  Copyright (c) 2013 Anthony Merrin. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef void(^ANTInputCompletionHandler) (NSString *inputText);

@interface ANTInputViewController : UIViewController

@property (copy, nonatomic) ANTInputCompletionHandler completionHandler;

@end
