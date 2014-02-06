//
//  ANTDetailViewController.m
//  Merrin_Anthony_Lab6_WebsiteApp
//
//  Created by Anthony Merrin on 11/18/13.
//  Copyright (c) 2013 Anthony Merrin. All rights reserved.
//

#import "ANTDetailViewController.h"
#import <Social/Social.h>

@interface ANTDetailViewController () <UIWebViewDelegate>
@property (strong, nonatomic) UIPopoverController *masterPopoverController;
@property (weak, nonatomic) IBOutlet UIWebView *websiteView;
@property (weak, nonatomic) IBOutlet UIActivityIndicatorView *activityIndicator;
@end



@implementation ANTDetailViewController

#pragma mark - Managing the detail item

- (void)setDetailItem:(id)newDetailItem
{
    if (_detailItem != newDetailItem) {
        _detailItem = newDetailItem;
        
        // Update the view.
        [self configureView];
    }
    
    if (_detailDescription != newDetailItem) {
        _detailDescription = newDetailItem;
    }
    
    if (self.masterPopoverController != nil) {
        [self.masterPopoverController dismissPopoverAnimated:YES];
    }
}

- (void)configureView
{
    // Update the user interface for the detail item.
    
    if (self.detailItem) {
        
        
        NSURL *url= [NSURL URLWithString: [self.detailItem stringByAddingPercentEscapesUsingEncoding:
                                           NSUTF8StringEncoding]];
        NSURLRequest *request = [NSURLRequest requestWithURL:url];

        [self.websiteView loadRequest:request];
    }
    
    if (self.detailDescription) {
        [self.navigationItem setTitle:self.detailDescription];
    }
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    [self configureView];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Split view

- (void)splitViewController:(UISplitViewController *)splitController willHideViewController:(UIViewController *)viewController withBarButtonItem:(UIBarButtonItem *)barButtonItem forPopoverController:(UIPopoverController *)popoverController
{
    barButtonItem.title = NSLocalizedString(@"List", @"List");
    [self.navigationItem setLeftBarButtonItem:barButtonItem animated:YES];
    self.masterPopoverController = popoverController;
}

- (void)splitViewController:(UISplitViewController *)splitController willShowViewController:(UIViewController *)viewController invalidatingBarButtonItem:(UIBarButtonItem *)barButtonItem
{
    // Called when the view is shown again in the split view, invalidating the button and popover controller.
    [self.navigationItem setLeftBarButtonItem:nil animated:YES];
    self.masterPopoverController = nil;
}

#pragma mark - Web View

-(void)webViewDidStartLoad:(UIWebView *)webView {
    [self.activityIndicator startAnimating];
}

-(void)webViewDidFinishLoad:(UIWebView *)webView {
    [self.activityIndicator stopAnimating];
}

#pragma mark - Facebook Sharing

- (IBAction)postToFacebook:(id)sender {
    {
        SLComposeViewController *controller = [SLComposeViewController composeViewControllerForServiceType:SLServiceTypeFacebook];
        [controller setInitialText:self.detailItem];
        [self presentViewController:controller animated:YES completion:Nil];
    }
}

@end
