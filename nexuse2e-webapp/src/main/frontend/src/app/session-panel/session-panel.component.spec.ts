import { ComponentFixture, TestBed, waitForAsync } from "@angular/core/testing";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TranslateModule } from "@ngx-translate/core";
import { DataService } from "../data/data.service";
import { SessionPanelComponent } from "./session-panel.component";
import { Router } from "@angular/router";
import { RouterTestingModule } from "@angular/router/testing";

describe("SessionPanelComponent", () => {
  let component: SessionPanelComponent;
  let fixture: ComponentFixture<SessionPanelComponent>;
  let dataService: DataService;
  let router: Router;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          HttpClientTestingModule,
          TranslateModule.forRoot(),
          RouterTestingModule,
        ],
        declarations: [SessionPanelComponent],
        providers: [DataService],
      });
      fixture = TestBed.createComponent(SessionPanelComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
      dataService = TestBed.inject(DataService);
      router = TestBed.inject(Router);
    })
  );

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should show username", async () => {
    spyOn(dataService, "get").and.returnValue(Promise.resolve("Administrator"));
    await component.getUsername();

    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector(".user-name")).toBeTruthy();
  });

  it("should redirect to login after logout", async () => {
    spyOn(dataService, "post").and.returnValue(Promise.resolve("200"));
    spyOn(router, "navigateByUrl");
    await component.logout();

    expect(router.navigateByUrl).toHaveBeenCalledWith("/login");
  });
});
