export interface Message {
  messageId: string;
  actionId: string;
  createdDate: Date;
  typeName: string;
  status: string;
  conversationId: string;
  nxMessageId: number;
  nxConversationId: number;
  partnerId: string;
}
