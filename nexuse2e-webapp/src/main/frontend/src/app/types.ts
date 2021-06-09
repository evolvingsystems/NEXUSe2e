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
  modifiedDate: string;
}

export interface ConversationDetail extends Conversation {
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

export interface Choreography extends NexusData {
  nxChoreographyId: number;
  name: string;
  lastInboundTime: string;
  lastOutboundTime: string;
}

export interface Partner extends NexusData {
  partnerId: string;
  nxPartnerId: number;
  name: string;
  lastInboundTime: string;
  lastOutboundTime: string;
}

export interface Certificate extends NexusData {
  name: string;
  timeUntilExpiry: string;
  nxCertificateId: number;
  configuredFor: string;
  nxPartnerId: number;
  validity: string;
  remainingDays: number;
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
  additionalLinkText?: string;
  linkUrlRecipe?: string;
  additionalLinkUrlRecipe?: string;
  additionalLinkQueryParamsRecipe?: { [s: string]: string };
  isHeader?: boolean;
  separator?: Separator;
  showCopyIcon?: boolean;
  titleCase?: boolean;
  iconUrlRecipe?: string;
  actionButton?: Action;
}

export enum ColumnType {
  TEXT,
  LONG_TEXT,
  LINK,
  ICON,
  ACTION_BUTTON,
  TEXT_AND_MORE,
  LINK_AND_MORE,
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

export interface EngineTimeVariables {
  dashboardTimeFrameInDays: number;
  transactionActivityTimeframeInWeeks: number;
  idleGracePeriodInMinutes: number;
}

export function isMessage(item: NexusData): item is Message {
  return (item as Message).actionId !== undefined;
}

export function isMessageDetail(item: NexusData): item is MessageDetail {
  return (item as MessageDetail).engineLogs !== undefined;
}

export function isConversation(item: NexusData): item is Conversation {
  return (item as Conversation).currentAction !== undefined;
}

export function isEngineLog(item: NexusData): item is EngineLog {
  return (item as EngineLog).methodName !== undefined;
}

export function isDateRange(item: unknown): item is DateRange {
  return (item as DateRange).startDate !== undefined;
}

export function isChoreography(item: NexusData): item is Choreography {
  return (item as Choreography).nxChoreographyId !== undefined;
}

export function isPartner(item: NexusData): item is Partner {
  return (item as Partner).lastInboundTime !== undefined;
}

export function isCertificate(item: NexusData): item is Certificate {
  return (item as Certificate).nxCertificateId !== undefined;
}
