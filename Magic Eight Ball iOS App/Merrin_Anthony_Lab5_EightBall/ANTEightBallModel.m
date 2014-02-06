//  
//  ANTModel.m
//  Magic Eight Ball iOS App
//
//  Created by Anthony Merrin on 9/18/13.
//  Copyright (c) 2013 ITP. All rights reserved.
//

#import "ANTEightBallModel.h"

NSString *const AnswerArray = @"AnswerArray";
NSString *const SecretAnswer = @"SecretAnswer";
NSString *const DataPlist = @"answers.plist";

@interface ANTEightBallModel ()
@property (strong,nonatomic) NSMutableArray *answers;
@property (strong,nonatomic) NSString *filepath;
@property (strong,nonatomic) NSMutableDictionary *plist;
@end

@implementation ANTEightBallModel
- (id)init
{
    self = [super init];
    if (self) {
        NSArray *paths = NSSearchPathForDirectoriesInDomains ( NSDocumentDirectory, NSUserDomainMask, YES);
        NSString *documentsDirectory = [paths objectAtIndex:0];
        _filepath = [documentsDirectory stringByAppendingPathComponent:DataPlist];
        _plist = [NSMutableDictionary dictionaryWithContentsOfFile:_filepath];
        if (!_plist) {
            _plist = [NSMutableDictionary dictionaryWithCapacity:2];
            _answers = [[NSMutableArray alloc]
                        initWithObjects:@"Try again later",
                        @"Unlikely",
                        @"Perhaps",
                        @"Definitely",
                        @"Its uncertain",
                        @"Looking good",
                        nil];
            [_plist setObject:_answers forKey:AnswerArray];
            _secretAnswer = @"Shoot for the stars!";
            [_plist setObject:_secretAnswer forKey:SecretAnswer];
            [self save];
        } else {
            _answers = [_plist objectForKey:AnswerArray];
            _secretAnswer = [_plist objectForKey:SecretAnswer];
        }
    }
    return self;
}

- (NSString *) randomAnswer {
    if ([self.answers count])
    {
        return [self.answers objectAtIndex:random()
                % [self.answers count]];
    }
    return @"I got nothing";
}

- (NSInteger) numberOfAnswers {
    return [self.answers count];
}

- (NSString *) answerAtIndex: (NSInteger) index {
    return [self.answers objectAtIndex: index];
}

- (void) removeAnswerAtIndex: (NSInteger) index {
    [self.answers removeObjectAtIndex: index];
    [self save];
}

- (void) addAnswer: (NSString *)answer AtIndex:(NSInteger) index{
    [self.answers insertObject:answer atIndex:index];
    [self save];
}

+ (instancetype) sharedModel
{
    static ANTEightBallModel *_sharedModel=nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        // code to be executed once - thread safe version!
        _sharedModel = [[self alloc] init];
    });
    return _sharedModel;
}

- (void) save
{
    [self.plist setObject:self.answers forKey:AnswerArray];
    [self.plist setObject:self.secretAnswer forKey:SecretAnswer];
    [self.plist writeToFile:self.filepath atomically:YES];
}
@end
