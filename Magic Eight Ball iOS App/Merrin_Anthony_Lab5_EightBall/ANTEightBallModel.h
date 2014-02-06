//
//  ANTModel.h
//  Magic Eight Ball iOS App
//
//  Created by Anthony Merrin on 9/18/13.
//  Copyright (c) 2013 ITP. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ANTEightBallModel : NSObject
@property (strong,nonatomic) NSString *secretAnswer;
- (NSString *) randomAnswer;
- (NSInteger) numberOfAnswers;
- (NSString *) answerAtIndex: (NSInteger) index;
- (void) removeAnswerAtIndex: (NSInteger) index;
- (void) addAnswer: (NSString *) answer AtIndex: (NSInteger) index;
+ (instancetype) sharedModel;
- (void) save;

@end
