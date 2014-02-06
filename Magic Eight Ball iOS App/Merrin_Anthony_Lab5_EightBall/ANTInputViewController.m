//
//  ANTViewController.m
//  InputText
//
//  Created by Anthony Merrin on 10/20/13.
//  Copyright (c) 2013 Anthony Merrin. All rights reserved.
//

#import "ANTInputViewController.h"

@interface ANTInputViewController () <UITextFieldDelegate>
@property (weak, nonatomic) IBOutlet UITextField *inputField;
@property (weak, nonatomic) IBOutlet UIBarButtonItem *saveButton;

@end

@implementation ANTInputViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
	self.saveButton.enabled = NO;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewWillAppear: (BOOL)animated
{
    [super viewWillAppear:animated];
    [self.inputField becomeFirstResponder];
}

- (BOOL) textFieldShouldReturn:(UITextField *)textField
{
    if (self.completionHandler)
    {
        self.completionHandler(self.inputField.text);
    }
    return YES;
}

- (BOOL)textField: (UITextField *) textField
        shouldChangeCharactersInRange:(NSRange)range
        replacementString:(NSString *)string
{
    NSString *changedString = [textField.text
                               stringByReplacingCharactersInRange: range
                               withString: string];
    [self validateSaveButtonForText: changedString];
    return YES;
}

- (void)validateSaveButtonForText: (NSString *)text
{
    self.saveButton.enabled = (text.length>0);
}

- (IBAction)saveButtonTapped:(id)sender
{
    if (self.completionHandler)
    {
        self.completionHandler(self.inputField.text);
    }
}

- (IBAction)cancelButtonTapped:(id)sender
{
    if (self.completionHandler)
    {
        self.completionHandler(nil);
    }
}
@end
