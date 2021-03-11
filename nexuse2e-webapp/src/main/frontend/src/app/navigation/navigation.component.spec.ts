import { ComponentFixture, TestBed, waitForAsync } from "@angular/core/testing";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TranslateModule } from "@ngx-translate/core";
import { NavigationComponent } from "./navigation.component";
import { RouterTestingModule } from "@angular/router/testing";
import { By } from "@angular/platform-browser";
import { DataService } from "../data/data.service";

describe("NavigationComponent", () => {
  let component: NavigationComponent;
  let fixture: ComponentFixture<NavigationComponent>;
  let dataService: DataService;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          HttpClientTestingModule,
          TranslateModule.forRoot(),
          RouterTestingModule,
        ],
        declarations: [NavigationComponent],
        providers: [DataService],
      });
      fixture = TestBed.createComponent(NavigationComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
      dataService = TestBed.inject(DataService);
    })
  );

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should show version information", async () => {
    spyOn(dataService, "getVersion").and.returnValue(
      Promise.resolve(["5.9.0", "Revision: 2.4.2", "Build: 14.2.4"])
    );
    await component.ngOnInit();
    fixture.detectChanges();

    const versionInfo = fixture.debugElement.query(By.css(".version-info"));
    expect(versionInfo.nativeElement.textContent).toContain("5.9.0");
    expect(versionInfo.nativeElement.textContent).toContain("Revision: 2.4.2");
    expect(versionInfo.nativeElement.textContent).toContain("Build: 14.2.4");
  });
});
