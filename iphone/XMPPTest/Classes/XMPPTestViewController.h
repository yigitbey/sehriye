//
//  XMPPTestViewController.h
//  XMPPTest
//
//  Created by Samet on 1/31/11.
//  Copyright 2011 JoopleBerry Inc. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface XMPPTestViewController : UIViewController {
	
	
	UITextField *recieverTextField;
	UITextField *messageField;
	UITextView *chatView;

}

@property (nonatomic, retain) IBOutlet UITextField *recieverTextField;
@property (nonatomic, retain) IBOutlet UITextField *messageField;
@property (nonatomic, retain) IBOutlet UITextField *chatTextView;


@end
