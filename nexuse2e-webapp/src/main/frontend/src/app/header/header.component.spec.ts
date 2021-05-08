import { ComponentFixture, TestBed, waitForAsync } from "@angular/core/testing";
import { HeaderComponent } from "./header.component";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { TranslateModule } from "@ngx-translate/core";
import { By } from "@angular/platform-browser";
import { DataService } from "../services/data.service";
import { MatIconModule } from "@angular/material/icon";
import { SessionPanelComponent } from "../session-panel/session-panel.component";
import { RouterTestingModule } from "@angular/router/testing";

describe("HeaderComponent", () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;
  let dataService: DataService;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          HttpClientTestingModule,
          TranslateModule.forRoot(),
          MatIconModule,
          RouterTestingModule,
        ],
        declarations: [HeaderComponent, SessionPanelComponent],
        providers: [DataService],
      });
      fixture = TestBed.createComponent(HeaderComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
      dataService = TestBed.inject(DataService);
    })
  );

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should show machine name and version number", async () => {
    spyOn(dataService, "getMachineName").and.returnValue(
      Promise.resolve("Machine Name")
    );
    spyOn(dataService, "getVersion").and.returnValue(
      Promise.resolve(["5.9.0", "Revision: 2.4.2", "Build: 14.2.4"])
    );
    await component.ngOnInit();
    fixture.detectChanges();

    const machineName = fixture.debugElement.query(By.css(".machine-name"));
    const version = fixture.debugElement.query(By.css(".version"));
    expect(machineName.nativeElement.textContent).toBe("Machine Name");
    expect(version.nativeElement.textContent).toBe("5.9.0");
  });
});
