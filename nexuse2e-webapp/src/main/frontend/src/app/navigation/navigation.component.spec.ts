import { ComponentFixture, TestBed, waitForAsync } from "@angular/core/testing";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TranslateModule } from "@ngx-translate/core";
import { NavigationComponent } from "./navigation.component";
import { RouterTestingModule } from "@angular/router/testing";
import { CacheService } from "../data/cache.service";
import { By } from "@angular/platform-browser";

describe("NavigationComponent", () => {
  let component: NavigationComponent;
  let fixture: ComponentFixture<NavigationComponent>;
  let cacheService: CacheService;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          HttpClientTestingModule,
          TranslateModule.forRoot(),
          RouterTestingModule,
        ],
        declarations: [NavigationComponent],
        providers: [CacheService],
      });
      fixture = TestBed.createComponent(NavigationComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
      cacheService = TestBed.inject(CacheService);
    })
  );

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should show version information", async () => {
    spyOn(cacheService, "get").and.returnValue(
      Promise.resolve(["5.9.0", "Revision: 2.4.2", "Build: 14.2.4"])
    );
    await component.getVersionInfo();
    fixture.detectChanges();

    const versionInfo = fixture.debugElement.query(By.css(".version-info"));
    expect(versionInfo.nativeElement.textContent).toContain("5.9.0");
    expect(versionInfo.nativeElement.textContent).toContain("Revision: 2.4.2");
    expect(versionInfo.nativeElement.textContent).toContain("Build: 14.2.4");
  });
});
