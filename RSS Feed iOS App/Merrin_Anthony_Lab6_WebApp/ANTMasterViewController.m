//
//  ANTMasterViewController.m
//  Merrin_Anthony_Lab6_WebsiteApp
//
//  Created by Anthony Merrin on 11/18/13.
//  Copyright (c) 2013 Anthony Merrin. All rights reserved.
//

#import "ANTMasterViewController.h"

#import "ANTDetailViewController.h"

#import "ANTInputViewController.h"

@interface ANTMasterViewController () {
    //NSMutableArray *_objects;
        NSMutableString *rssTitle;
        NSMutableString *rssLink;
        NSString *rssElement;
        NSXMLParser *rssParser;
        NSMutableArray *rssFeeds;
        NSMutableDictionary *rssItem;
}
@end

@implementation ANTMasterViewController

- (void)awakeFromNib
{
    self.clearsSelectionOnViewWillAppear = NO;
    self.preferredContentSize = CGSizeMake(320.0, 600.0);
    [super awakeFromNib];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
    self.detailViewController = (ANTDetailViewController *)[[self.splitViewController.viewControllers lastObject] topViewController];
    
    NSString *path = [[NSBundle mainBundle] pathForResource:@"Websites" ofType:@"plist"];
    self.websites = [NSDictionary dictionaryWithContentsOfFile:path];
    self.names = [self.websites keysSortedByValueUsingSelector:@selector(compare:)];
    
    rssFeeds = [[NSMutableArray alloc] init];
    NSURL *url = [NSURL URLWithString:@"http://news.nationalgeographic.com/index.rss"];
    rssParser = [[NSXMLParser alloc] initWithContentsOfURL:url];
    [rssParser setDelegate:self];
    [rssParser setShouldResolveExternalEntities:NO];
    [rssParser parse];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)insertNewObject:(id)sender
{
    /*
     if (!_objects) {
     _objects = [[NSMutableArray alloc] init];
     }
     [_objects insertObject:[NSDate date] atIndex:0];
     NSIndexPath *indexPath = [NSIndexPath indexPathForRow:0 inSection:0];
     [self.tableView insertRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationAutomatic];
     */
}

#pragma mark - Table View

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.rssLinks.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"WebsiteCell" forIndexPath:indexPath];
    
    NSDictionary *linkInfo = [self.rssLinks objectAtIndex:indexPath.row];
    cell.textLabel.text = [linkInfo objectForKey:@"title"];
    return cell;
}

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the specified item to be editable.
    return NO;
}

- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
    /*
     if (editingStyle == UITableViewCellEditingStyleDelete) {
     [_objects removeObjectAtIndex:indexPath.row];
     [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
     } else if (editingStyle == UITableViewCellEditingStyleInsert) {
     // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view.
     }
     */
}

/*
 // Override to support rearranging the table view.
 - (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)fromIndexPath toIndexPath:(NSIndexPath *)toIndexPath
 {
 }
 */

/*
 // Override to support conditional rearranging of the table view.
 - (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath
 {
 // Return NO if you do not want the item to be re-orderable.
 return YES;
 }
 */

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSDictionary *currArticle = [self.rssLinks objectAtIndex:indexPath.row];
    self.detailViewController.detailItem = [currArticle valueForKey:@"link"];
    self.detailViewController.detailDescription = [currArticle valueForKey:@"title"];
}

#pragma mark - RSS Parser

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName attributes:(NSDictionary *)attributeDict
{
    rssElement = elementName;
    if([rssElement isEqualToString:@"item"])
    {
        rssItem = [[NSMutableDictionary alloc] init];
        rssTitle = [[NSMutableString alloc] init];
        rssLink = [[NSMutableString alloc] init];
    }
}

- (void)parser:(NSXMLParser *)parser foundCharacters:(NSString *)string
{
    if([rssElement isEqualToString:@"title"])
    {
        [rssTitle appendString:string];
    }
    else if([rssElement isEqualToString:@"link"])
    {
        [rssLink appendString:string];
    }
}

- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName
{
    if([elementName isEqualToString:@"item"])
    {
        [rssItem setObject:rssTitle forKey:@"title"];
        [rssItem setObject:rssLink forKey:@"link"];
        [rssFeeds addObject:[rssItem copy]];
    }
}

- (void)parserDidEndDocument:(NSXMLParser *)parser
{
    [self.tableView reloadData];
    NSArray *path = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentFolder = [path objectAtIndex:0];
    NSString *filePath = [documentFolder stringByAppendingFormat:@"myfile.plist"];
    [rssFeeds writeToFile:filePath atomically:YES];
    self.rssLinks = [NSArray arrayWithContentsOfFile:filePath];
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    ANTInputViewController *inputVC = segue.destinationViewController;
    inputVC.completionHandler = ^(NSString *text)
    {
        if (text != nil)
        {
            NSURL *url = [NSURL URLWithString:text];
            rssParser = [[NSXMLParser alloc] initWithContentsOfURL:url];
            [rssParser setDelegate:self];
            [rssParser setShouldResolveExternalEntities:NO];
            [rssParser parse];
            [self.tableView reloadData];
        }
        [self dismissViewControllerAnimated:YES completion:nil];
    };
}

@end
