import { Action, ColumnConfig, ColumnType } from "../types";

// START DISPLAY CONFIG
export const CONV_DETAIL__CONVERSATION_CONFIG: ColumnConfig[] = [
  { columnType: ColumnType.TEXT, fieldName: "conversationId" },
  {
    columnType: ColumnType.TEXT,
    fieldName: "createdDate",
  },
  {
    columnType: ColumnType.TEXT,
    fieldName: "modifiedDate",
  },
  {
    columnType: ColumnType.TEXT,
    fieldName: "endDate",
  },
  {
    columnType: ColumnType.TEXT,
    fieldName: "turnAroundTime",
  },
  {
    columnType: ColumnType.TEXT,
    fieldName: "partnerId",
  },
  {
    columnType: ColumnType.TEXT,
    fieldName: "currentAction",
  },
  {
    columnType: ColumnType.TEXT,
    fieldName: "status",
    titleCase: true,
  },
];

export const CONV_DETAIL__MESSAGE_CONFIG: ColumnConfig[] = [
  {
    columnType: ColumnType.LINK,
    fieldName: "messageId",
    linkUrlRecipe: "../../message/$nxMessageId$",
  },
  { columnType: ColumnType.TEXT, fieldName: "typeName", label: "messageType" },
  {
    columnType: ColumnType.TEXT,
    fieldName: "choreographyId",
    additionalFieldName: "actionId",
    label: "step",
  },
  { columnType: ColumnType.TEXT, fieldName: "direction", titleCase: true },
  { columnType: ColumnType.TEXT, fieldName: "createdDate" },
  { columnType: ColumnType.TEXT, fieldName: "endDate" },
  { columnType: ColumnType.TEXT, fieldName: "turnAroundTime" },
  { columnType: ColumnType.TEXT, fieldName: "status", titleCase: true },
];

export const CONV_DETAIL__LOG_CONFIG: ColumnConfig[] = [
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
// END DISPLAY CONFIG

// START ACTIONS
export const CONV_DETAIL__ACTIONS: Action[] = [
  {
    label: "delete",
    icon: "delete",
    actionKey: "/conversation/delete",
  },
];
// END ACTIONS
