import { ComponentFixture, TestBed, waitForAsync } from "@angular/core/testing";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TranslateModule } from "@ngx-translate/core";
import { DataService } from "../data/data.service";
import { SessionPanelComponent } from "./session-panel.component";
import { Router } from "@angular/router";
import { RouterTestingModule } from "@angular/router/testing";
import { By } from "@angular/platform-browser";

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
    spyOn(dataService, "getFullUsername").and.returnValue(Promise.resolve("Administrator"));
    await component.ngOnInit();
    fixture.detectChanges();

    const userName = fixture.debugElement.query(By.css(".user-name"));
    expect(userName.nativeElement.textContent.trim()).toBe("Administrator");
  });

  it("should redirect to login after logout", async () => {
    spyOn(dataService, "post").and.returnValue(Promise.resolve("200"));
    spyOn(router, "navigateByUrl");
    await component.logout();

    expect(router.navigateByUrl).toHaveBeenCalledWith("/login");
  });
});
