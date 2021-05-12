import { Action, ListConfig } from "../types";

export const MESS_DETAIL__MESSAGE_CONFIG: ListConfig[] = [
  {
    fieldName: "messageId",
  },
  {
    fieldName: "conversationId",
    linkUrlRecipe: "../../conversation/$nxConversationId$",
  },
  { fieldName: "choreographyId" },
  { fieldName: "partnerId" },
  { fieldName: "typeName", label: "messageType" },
  { fieldName: "direction" },
  { fieldName: "referencedMessageId" },
  { fieldName: "actionId" },
  { fieldName: "backendStatus" },
  { fieldName: "createdDate" },
  { fieldName: "modifiedDate" },
  { fieldName: "endDate" },
  { fieldName: "turnAroundTime" },
  { fieldName: "expirationDate" },
  { fieldName: "retries" },
  { fieldName: "trp", label: "protocolVersion" },
  { fieldName: "status" },
];

export const MESS_DETAIL__LOG_CONFIG: ListConfig[] = [
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
