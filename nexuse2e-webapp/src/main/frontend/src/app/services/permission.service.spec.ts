import { TestBed } from "@angular/core/testing";

import { PermissionService } from "./permission.service";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { SessionService } from "./session.service";

describe("PermissionService", () => {
  let service: PermissionService;
  let sessionService: SessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    service = TestBed.inject(PermissionService);
    sessionService = TestBed.inject(SessionService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });

  it("should permit all actions if user has a wildcard", () => {
    spyOn(sessionService, "getPermittedActions").and.returnValue(["*"]);
    expect(service.isUserPermitted("requeue")).toEqual(true);
  });

  it("should permit an action if it is contained in the permitted actions", () => {
    spyOn(sessionService, "getPermittedActions").and.returnValue(["requeue", "stop"]);
    expect(service.isUserPermitted("requeue")).toEqual(true);
  });

  it("should not permit an action if it is not in the permitted actions", () => {
    spyOn(sessionService, "getPermittedActions").and.returnValue(["requeue", "stop"]);
    expect(service.isUserPermitted("delete")).toEqual(false);
  });
});
