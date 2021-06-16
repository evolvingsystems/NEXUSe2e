import { Action, ColumnConfig, ColumnType } from "../types";

export const MESS_DETAIL__MESSAGE_CONFIG: ColumnConfig[] = [
  {
    columnType: ColumnType.TEXT,
    fieldName: "messageId",
  },
  {
    columnType: ColumnType.LINK,
    fieldName: "conversationId",
    linkUrlRecipe: "../../conversation/$nxConversationId$",
  },
  { columnType: ColumnType.TEXT, fieldName: "choreographyId" },
  { columnType: ColumnType.TEXT, fieldName: "partnerId" },
  { columnType: ColumnType.TEXT, fieldName: "typeName", label: "messageType" },
  { columnType: ColumnType.TEXT, fieldName: "direction", titleCase: true },
  { columnType: ColumnType.TEXT, fieldName: "referencedMessageId" },
  { columnType: ColumnType.TEXT, fieldName: "actionId" },
  { columnType: ColumnType.TEXT, fieldName: "backendStatus", titleCase: true },
  { columnType: ColumnType.TEXT, fieldName: "createdDate" },
  { columnType: ColumnType.TEXT, fieldName: "modifiedDate" },
  { columnType: ColumnType.TEXT, fieldName: "endDate" },
  { columnType: ColumnType.TEXT, fieldName: "turnAroundTime" },
  { columnType: ColumnType.TEXT, fieldName: "expirationDate" },
  { columnType: ColumnType.TEXT, fieldName: "retries" },
  { columnType: ColumnType.TEXT, fieldName: "trp", label: "protocolVersion" },
  { columnType: ColumnType.TEXT, fieldName: "status", titleCase: true },
];

export const MESS_DETAIL__LOG_CONFIG: ColumnConfig[] = [
  {
    columnType: ColumnType.TEXT,
    fieldName: "severity",
  },
  {
    columnType: ColumnType.TEXT,
    fieldName: "createdDate",
  },
  {
    columnType: ColumnType.LONG_TEXT,
    fieldName: "description",
    showCopyIcon: true,
  },
  {
    columnType: ColumnType.TEXT,
    fieldName: "origin",
  },
  {
    columnType: ColumnType.TEXT,
    fieldName: "className",
  },
  {
    columnType: ColumnType.TEXT,
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
