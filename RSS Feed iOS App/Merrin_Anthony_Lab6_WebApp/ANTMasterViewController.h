//
//  ANTMasterViewController.h
//  RSS Reader iOS App
//
//  Created by Anthony Merrin on 11/20/13.
//  Copyright (c) 2013 Anthony Merrin. All rights reserved.
//

#import <UIKit/UIKit.h>

@class ANTDetailViewController;

@interface ANTMasterViewController : UITableViewController <NSXMLParserDelegate>

@property (strong, nonatomic) ANTDetailViewController *detailViewController;
@property (strong, nonatomic) NSDictionary *websites;
@property (strong, nonatomic) NSArray *names;
@property (strong, nonatomic) NSArray *rssLinks;
@property (weak, nonatomic) NSString *rssURL;

@end
