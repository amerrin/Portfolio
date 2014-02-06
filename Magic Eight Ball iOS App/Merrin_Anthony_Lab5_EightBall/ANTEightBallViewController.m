//
//  ANTViewController.m
//  Magic Eight Ball iOS App
//
//  Created by Anthony Merrin on 9/18/13.
//  Copyright (c) 2013 ITP. All rights reserved.
//

#import "ANTEightBallViewController.h"
#import "ANTEightBallModel.h"
#import "ANTInputViewController.h"

@interface ANTEightBallViewController ()
@property (weak, nonatomic) IBOutlet UILabel *answerTF;
@property (strong, nonatomic) ANTEightBallModel *model;
@end

@implementation ANTEightBallViewController
- (IBAction)eightBallTouched:(id)sender
{
    [self displayAnswerWithText:[self.model randomAnswer]];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	
    self.model = [ANTEightBallModel sharedModel];
    
    UITapGestureRecognizer *doubleTap = [[UITapGestureRecognizer alloc]
                                         initWithTarget: self
                                         action:@selector(doubleTapRecognized:)];
    doubleTap.numberOfTapsRequired = 2;
    [self.view addGestureRecognizer:doubleTap];
    
    UITapGestureRecognizer *singleTap = [[UITapGestureRecognizer alloc]
                                         initWithTarget: self
                                         action:@selector(singleTapRecognized:)];
    
    singleTap.numberOfTapsRequired = 1;
    [self.view addGestureRecognizer:singleTap];
    
    [singleTap requireGestureRecognizerToFail:doubleTap];
                                         
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)doubleTapRecognized: (UITapGestureRecognizer *)recognizer
{
    [self displayAnswerWithText:[self.model secretAnswer]];
}

- (void)singleTapRecognized: (UITapGestureRecognizer *)recognizer
{
    [self displayAnswerWithText:[self.model randomAnswer]];
}

- (void)displayAnswerWithText: (NSString *) answer
{
    [UIView animateWithDuration: 1.0
                     animations:^{
                         self.answerTF.alpha = 0;
                     }
                     completion:^ (BOOL finished){
                         [self animateAnswer: answer];
                     }];
}

- (void)animateAnswer: (NSString *) answer
{
    [UIView animateWithDuration: 1.0
                     animations:^{
                         self.answerTF.text = answer;
                         if (self.answerTF.textColor == UIColor.blackColor)
                         {
                             self.answerTF.textColor = [UIColor colorWithRed:(153.0f/255.0f) green:0.0 blue: 0.0 alpha: 1.0];
                         } else
                         {
                             self.answerTF.textColor = UIColor.blackColor;
                         }
                         self.answerTF.alpha = 1;
                     }];
}

- (BOOL) canBecomeFirstResponder
{
    return YES;
}

- (void) viewDidAppear:(BOOL)animated
{
    [self becomeFirstResponder];
}

- (void) motionEnded:(UIEventSubtype) motion withEvent:(UIEvent *) event
{
    if (motion == UIEventSubtypeMotionShake)
    {
        [self displayAnswerWithText:self.model.randomAnswer];
    }
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    ANTInputViewController *inputVC = segue.destinationViewController;
    inputVC.completionHandler = ^(NSString *text)
    {
        if (text != nil)
        {
            self.model.secretAnswer = text;
            [self.model save];
        }
        [self dismissViewControllerAnimated:YES completion:nil];
    };
}

@end
