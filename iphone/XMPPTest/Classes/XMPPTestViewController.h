//
//  XMPPTestViewController.h
//  XMPPTest
//
//  Created by Samet on 1/31/11.
//  Copyright 2011 JoopleBerry Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "XMPP.h"

@class XMPPStream;
@protocol CLLocationManagerDelegate;
@interface XMPPTestViewController : UIViewController <UITextFieldDelegate, UITableViewDelegate, UITableViewDataSource, UIScrollViewDelegate, CLLocationManagerDelegate> {
	
	
	UITextField *recieverTextField;
	UITextField *messageField;
	UITextView *chatTextView;
	UIButton *switchConnectionButton;
	XMPPStream * xmppStream;
	
	UITableView *chatTableView;
	UIScrollView *buttonsScroll;
	
	NSMutableArray *chatLog;
	
	NSMutableString *currentStranger;
	
	//Location Variables
	NSMutableString *currentLat;
	NSMutableString *currentLong;
	
	BOOL isTyping;

}

@property (nonatomic, retain) IBOutlet UITextField *recieverTextField;
@property (nonatomic, retain) IBOutlet UITextField *messageField;
@property (nonatomic, retain) IBOutlet UITextView *chatTextView;
@property (nonatomic, retain) IBOutlet UIButton *switchConnectionButton;

@property (nonatomic, retain) IBOutlet UITableView *chatTableView;
@property (nonatomic, retain) IBOutlet UIScrollView *buttonsScroll;

@property (nonatomic, retain) NSMutableArray *chatLog;
@property (nonatomic, retain) NSMutableString *currentStranger;

@property (nonatomic, retain) NSMutableString *currentLat;
@property (nonatomic, retain) NSMutableString *currentLong;

- (void)sendMessage:(id)sender;
- (void)connect:(id)sender;
- (void)disconnect:(id)sender;
- (void)getCurrentCoordinate;
- (void)switchConnection:(id)sender;

@end
