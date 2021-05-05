export interface NexusData {
}

export interface Message extends NexusData {
  messageId: string;
  choreographyId: string;
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

export interface Conversation extends NexusData {
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

export interface ActiveFilterList {
  [fieldName: string]: string | DateRange | undefined;
}
