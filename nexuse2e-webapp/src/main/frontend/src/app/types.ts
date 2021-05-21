export interface NexusData {
  status?: string;
  severity?: string;
  createdDate: string;
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

export interface MessageDetail extends Message {
  direction: string;
  modifiedDate: string;
  endDate: string;
  referencedMessageId: string;
  retries: string;
  expirationDate: string;
  trp: string;
  messagePayloads: Payload[];
  messageLabels: ReadonlyMap<string, string>;
  engineLogs: EngineLog[];
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

export interface ConversationDetail extends Conversation {
  modifiedDate: string;
  endDate: string;
  messages: Message[];
  engineLogs: EngineLog[];
}

export interface EngineLog extends NexusData {
  description: string;
  createdDate: string;
  severity: string;
  className: string;
  methodName: string;
}

export interface DateRange {
  startDate: Date | undefined;
  endDate: Date | undefined;
}

export interface ActiveFilterList {
  [fieldName: string]: string | DateRange | undefined;
}

export interface Action {
  label: string;
  icon?: string;
  actionKey: string;
}

export interface Payload {
  mimeType: string;
  contentId: string;
  id: number;
}

export interface PayloadParams {
  choreographyId: string;
  partnerId: string;
  conversationId: string;
  messageId: string;
  payloadId: string | undefined;
}

export interface NotificationItem {
  snackType: string;
  textLabel: string;
}

export interface LoginData {
  user: string;
  password: string;
}

export enum FilterType {
  TEXT,
  SELECT,
  DATE_TIME_RANGE,
}

export interface Filter {
  fieldName: string;
  filterType: FilterType;
  allowedValues?: string[];
  defaultValue?: string | DateRange;
}

export interface ColumnConfig {
  columnType: ColumnType;
  fieldName: string;
  additionalFieldName?: string;
  label?: string;
  linkUrlRecipe?: string;
  additionalLinkUrlRecipe?: string;
  isHeader?: boolean;
  separator?: Separator;
  showCopyIcon?: boolean;
}

export enum ColumnType {
  BASIC,
  LONG_TEXT,
  LINK,
  SPLIT_WITH_BASIC,
}

export enum Separator {
  VERTICAL_BAR,
  BRACKETS,
}

export interface ListModalDialog {
  items: NexusData[];
  itemType: string;
  mobileConfig: ColumnConfig[];
  desktopConfig: ColumnConfig[];
}

export interface UserConfirmationDialog {
  notificationTitleLabel?: string;
  notificationTextLabel?: string;
  confirmButtonLabel?: string;
}
