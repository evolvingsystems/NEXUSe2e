import { ComponentFixture, TestBed, waitForAsync } from "@angular/core/testing";
import { HeaderComponent } from "./header.component";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TranslateModule } from "@ngx-translate/core";
import { CacheService } from "../data/cache.service";
import { By } from "@angular/platform-browser";

describe("HeaderComponent", () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;
  let cacheService: CacheService;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule, TranslateModule.forRoot()],
        declarations: [HeaderComponent],
        providers: [CacheService],
      });
      fixture = TestBed.createComponent(HeaderComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
      cacheService = TestBed.inject(CacheService);
    })
  );

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should show machine name and version number", async () => {
    spyOn(cacheService, "get")
      .withArgs("/machine-name")
      .and.returnValue(Promise.resolve("Machine Name"))
      .withArgs("/version")
      .and.returnValue(
        Promise.resolve(["5.9.0", "Revision: 2.4.2", "Build: 14.2.4"])
      );
    await component.getMachineName();
    await component.getVersionNumber();

    fixture.detectChanges();
    const machineName = fixture.debugElement.query(By.css(".machine-name"));
    const version = fixture.debugElement.query(By.css(".version"));
    expect(machineName.nativeElement.textContent).toBe("Machine Name");
    expect(version.nativeElement.textContent).toBe("5.9.0");
  });
});
