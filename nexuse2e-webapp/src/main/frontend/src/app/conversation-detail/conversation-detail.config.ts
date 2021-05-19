import { Action, ColumnConfig, ColumnType } from "../types";

// START DISPLAY CONFIG
export const CONV_DETAIL__CONVERSATION_CONFIG: ColumnConfig[] = [
  { columnType: ColumnType.BASIC, fieldName: "conversationId" },
  {
    columnType: ColumnType.BASIC,
    fieldName: "createdDate",
  },
  {
    columnType: ColumnType.BASIC,
    fieldName: "modifiedDate",
  },
  {
    columnType: ColumnType.BASIC,
    fieldName: "endDate",
  },
  {
    columnType: ColumnType.BASIC,
    fieldName: "turnAroundTime",
  },
  {
    columnType: ColumnType.BASIC,
    fieldName: "partnerId",
  },
  {
    columnType: ColumnType.BASIC,
    fieldName: "currentAction",
  },
  {
    columnType: ColumnType.BASIC,
    fieldName: "status",
  },
];

export const CONV_DETAIL__MESSAGE_CONFIG: ColumnConfig[] = [
  {
    columnType: ColumnType.LINK,
    fieldName: "messageId",
    linkUrlRecipe: "../../message/$nxMessageId$",
  },
  { columnType: ColumnType.BASIC, fieldName: "typeName", label: "messageType" },
  {
    columnType: ColumnType.BASIC,
    fieldName: "choreographyId",
    additionalFieldName: "actionId",
    label: "step",
  },
  { columnType: ColumnType.BASIC, fieldName: "direction" },
  { columnType: ColumnType.BASIC, fieldName: "createdDate" },
  { columnType: ColumnType.BASIC, fieldName: "endDate" },
  { columnType: ColumnType.BASIC, fieldName: "turnAroundTime" },
  { columnType: ColumnType.BASIC, fieldName: "status" },
];

export const CONV_DETAIL__LOG_CONFIG: ColumnConfig[] = [
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
