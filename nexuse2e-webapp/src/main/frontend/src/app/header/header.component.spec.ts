import { ComponentFixture, TestBed, waitForAsync } from "@angular/core/testing";
import { HeaderComponent } from "./header.component";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TranslateModule } from "@ngx-translate/core";
import { CacheService } from "../data/cache.service";

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
      .and.returnValue(Promise.resolve("Hallo Welt"))
      .withArgs("/version")
      .and.returnValue(
        Promise.resolve(["5.9.0", "Revision: 2.4.2", "Build: 14.2.4"])
      );
    await component.getMachineName();
    await component.getVersionNumber();

    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector(".machine-name")).toBeTruthy();
    expect(fixture.nativeElement.querySelector(".version")).toBeTruthy();
  });
});
