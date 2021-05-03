export interface Message {
  messageId: string;
  actionId: string;
  createdDate: string;
  typeName: string;
  status: string;
  conversationId: string;
  nxMessageId: number;
  nxConversationId: number;
  partnerId: string;
  backendStatus: string;
  turnAroundTime: string;
}

export interface Conversation {
  choreographyId: string;
  conversationId: string;
  nxConversationId: number;
  partnerId: string;
  createdDate: string;
  status: string;
  currentAction: string;
  turnAroundTime: string;
}

export interface DateRange {
  startDate: Date | undefined;
  endDate: Date | undefined;
}
