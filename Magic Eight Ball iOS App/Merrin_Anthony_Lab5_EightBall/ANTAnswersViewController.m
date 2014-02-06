//
//  ANTAnswersViewController.m
//  TableView
//
//  Created by Anthony Merrin on 10/20/13.
//  Copyright (c) 2013 Anthony Merrin. All rights reserved.
//

#import "ANTAnswersViewController.h"
#import "ANTEightBallModel.h"
#import "ANTInputViewController.h"

@interface ANTAnswersViewController ()

@property (strong, nonatomic)ANTEightBallModel* model;

@end

@implementation ANTAnswersViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.model = [ANTEightBallModel sharedModel];
    self.navigationItem.leftBarButtonItem = self.editButtonItem;
}
    
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.model numberOfAnswers];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"AnswerCell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    cell.textLabel.text = [self.model answerAtIndex:indexPath.row];
    return cell;
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (editingStyle == UITableViewCellEditingStyleDelete)
    {
        [self.model removeAnswerAtIndex:indexPath.row];
        [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
    }
    else if (editingStyle == UITableViewCellEditingStyleInsert)
    {
        
    }
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    ANTInputViewController *inputVC = segue.destinationViewController;
    inputVC.completionHandler = ^(NSString *text)
    {
        if (text != nil)
        {
            NSInteger answerIndex = [self.model numberOfAnswers];
            [self.model addAnswer: text AtIndex: answerIndex];
            NSIndexPath *indexPath = [NSIndexPath indexPathForRow:answerIndex
                                                        inSection:0];
            [self.tableView insertRowsAtIndexPaths:@[indexPath]
                                  withRowAnimation:UITableViewRowAnimationFade];
        }
        [self dismissViewControllerAnimated:YES completion:nil];
    };
}

@end
