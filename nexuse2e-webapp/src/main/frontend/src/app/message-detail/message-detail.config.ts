import { Action, ColumnConfig, ColumnType } from "../types";

export const MESS_DETAIL__MESSAGE_CONFIG: ColumnConfig[] = [
  {
    columnType: ColumnType.BASIC,
    fieldName: "messageId",
  },
  {
    columnType: ColumnType.LINK,
    fieldName: "conversationId",
    linkUrlRecipe: "../../conversation/$nxConversationId$",
  },
  { columnType: ColumnType.BASIC, fieldName: "choreographyId" },
  { columnType: ColumnType.BASIC, fieldName: "partnerId" },
  { columnType: ColumnType.BASIC, fieldName: "typeName", label: "messageType" },
  { columnType: ColumnType.BASIC, fieldName: "direction" },
  { columnType: ColumnType.BASIC, fieldName: "referencedMessageId" },
  { columnType: ColumnType.BASIC, fieldName: "actionId" },
  { columnType: ColumnType.BASIC, fieldName: "backendStatus" },
  { columnType: ColumnType.BASIC, fieldName: "createdDate" },
  { columnType: ColumnType.BASIC, fieldName: "modifiedDate" },
  { columnType: ColumnType.BASIC, fieldName: "endDate" },
  { columnType: ColumnType.BASIC, fieldName: "turnAroundTime" },
  { columnType: ColumnType.BASIC, fieldName: "expirationDate" },
  { columnType: ColumnType.BASIC, fieldName: "retries" },
  { columnType: ColumnType.BASIC, fieldName: "trp", label: "protocolVersion" },
  { columnType: ColumnType.BASIC, fieldName: "status" },
];

export const MESS_DETAIL__LOG_CONFIG: ColumnConfig[] = [
  {
    columnType: ColumnType.BASIC,
    fieldName: "severity",
  },
  {
    columnType: ColumnType.BASIC,
    fieldName: "createdDate",
  },
  {
    columnType: ColumnType.LONG_TEXT,
    fieldName: "description",
  },
  {
    columnType: ColumnType.BASIC,
    fieldName: "origin",
  },
  {
    columnType: ColumnType.BASIC,
    fieldName: "className",
  },
  {
    columnType: ColumnType.BASIC,
    fieldName: "methodName",
  },
];

export const MESS_DETAIL__ACTIONS: Action[] = [
  {
    label: "requeue",
    icon: "refresh",
    actionKey: "/message/requeue",
  },
  {
    label: "stop",
    icon: "stop",
    actionKey: "/message/stop",
  },
];
