import { Action, ListConfig } from "../types";

// START DISPLAY CONFIG
export const conversationConfig: ListConfig[] = [
  {
    fieldName: "conversationId",
  },
  {
    fieldName: "createdDate",
  },
  {
    fieldName: "modifiedDate",
  },
  {
    fieldName: "endDate",
  },
  {
    fieldName: "turnAroundTime",
  },
  {
    fieldName: "partnerId",
  },
  {
    fieldName: "currentAction",
  },
  {
    fieldName: "status",
  },
];

export const messageConfig: ListConfig[] = [
  {
    fieldName: "messageId",
    linkUrlRecipe: "../../message/$nxMessageId$",
  },
  { fieldName: "typeName", label: "messageType" },
  {
    fieldName: "choreographyId",
    additionalFieldName: "actionId",
    label: "step",
  },
  { fieldName: "direction" },
  { fieldName: "createdDate" },
  { fieldName: "endDate" },
  { fieldName: "turnAroundTime" },
  { fieldName: "status" },
];

export const logConfig: ListConfig[] = [
  {
    fieldName: "severity",
  },
  {
    fieldName: "createdDate",
  },
  {
    fieldName: "description",
  },
  {
    fieldName: "origin",
  },
  {
    fieldName: "className",
  },
  {
    fieldName: "methodName",
  },
];
// END DISPLAY CONFIG

// START ACTIONS
export const actions: Action[] = [
  {
    label: "delete",
    icon: "delete",
    actionKey: "/conversation/delete",
  },
];
// END ACTIONS
