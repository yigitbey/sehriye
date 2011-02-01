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

@interface XMPPTestViewController : UIViewController <UITextFieldDelegate> {
	
	
	UITextField *recieverTextField;
	UITextField *messageField;
	UITextView *chatTextView;
	UIButton *sendButton;
	XMPPStream * xmppStream;

}

@property (nonatomic, retain) IBOutlet UITextField *recieverTextField;
@property (nonatomic, retain) IBOutlet UITextField *messageField;
@property (nonatomic, retain) IBOutlet UITextView *chatTextView;
@property (nonatomic, retain) IBOutlet UIButton *sendButton;

- (void)sendMessage:(id)sender;

@end
