import { Component, OnInit, OnDestroy } from "@angular/core";
import { CommonModule, NgClass } from "@angular/common";
import { ApiService } from "../../../shared/services/api.service";
import { Router } from "@angular/router";
import { HttpClient } from "@angular/common/http";
import { FormsModule } from '@angular/forms';
import { Subject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

export interface ReportMeterReading {
  id?: number;
  salutation? : string;
  customerName?: string;
  customerEmail?: string;
  deliveryId?: number;
  orderId?: number;
  connectionId?: number;
  category?: string;
  readingDate?: string;
  meterReading?: string;
  imagePath?: string;
  status?: number;
  createdAt?: string;
  bookingId?: number;
  bookingStatus?: string;
  bookingCreatedOn?: number;
}

@Component({
  selector: 'app-report-meter-reading',
  imports: [
    CommonModule,
    NgClass,
    FormsModule
  ],
  standalone: true,
  templateUrl: './report-meter-reading.component.html',
  styleUrl: './report-meter-reading.component.css',
})
export class ReportMeterReadingComponent implements OnInit {

  reportMeterReadings: ReportMeterReading[] = [];
  filteredReportMeterReadings: ReportMeterReading[] = [];

  isLoading = false;
  errorMessage = '';

  selectedReading: any = null;
  isSidebarOpen = false;

  // filterStatus = 0;
  categories: string[] = [];
  selectedCategory = '';
  isFilterOpen = false;

  searchTerm = '';
  private searchTerm$ = new Subject<string>();
  private searchSub!: Subscription;
  
  readonly IMAGE_BASE_URL = 'http://192.168.0.155:8080/assets/customers/';
  isImageModalOpen = false;

  constructor(
    // private api: ApiService,
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.searchSub = this.searchTerm$
      .pipe(
        debounceTime(350),
        distinctUntilChanged()
      )
      .subscribe(() => {
        this.fetchReportMeterReadings();
      });
    this.fetchReportMeterReadings();
  }

  ngOnDestroy(): void {
    this.searchSub?.unsubscribe();
  }

  onSearchInput(value: string): void {
    this.searchTerm = value;
    this.searchTerm$.next(value);
  }

  clearSearch(): void {
    this.searchTerm = '';
    this.searchTerm$.next('');
  }

  fetchReportMeterReadings(): void {

    this.isLoading = true;
    this.errorMessage = '';

    const payload = {
      search: this.searchTerm?.trim() || ''
    };

    this.http.post('http://192.168.0.155:8080/admin/report-meter-reading',payload)
      .subscribe({
        next: (res: any) => {

          this.isLoading = false;

          if (Array.isArray(res)) {
            this.reportMeterReadings = res;
              this.categories = [
                ...new Set(
                  this.reportMeterReadings
                    .map(x => x.category)
                    .filter((category): category is string => !!category)
                )
              ];
              console.log('Categories:', this.categories);
              console.log('Data:', this.reportMeterReadings);
              this.applyCategoryFilter();
          } else if (res?.data) {
            this.reportMeterReadings = res.data;
          } else {
            this.reportMeterReadings = [];
          }
        },

        error: (err : any) => {

          this.isLoading = false;
          this.errorMessage =
            'Fehler beim Laden der Messwertmeldungen';

          console.error(err);
        }
      });
  }

  formatDate(date?: string): string {

    if (!date) return '—';

    return new Intl.DateTimeFormat('de-DE', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    }).format(new Date(date));
  }

   formatDate2(timestamp: number | string | Date): string {
    if (!timestamp) return "—";

    let date: Date;

    if (typeof timestamp === "number") {
      date =
        timestamp.toString().length === 10
          ? new Date(timestamp * 1000)
          : new Date(timestamp);
    } else {
      date = new Date(timestamp);
    }

    if (isNaN(date.getTime())) return "—";

    return date.toLocaleDateString("de-DE", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  }
  
  getActiveCount(): number {
    return this.reportMeterReadings.filter(
      item => item.status === 1
    ).length;
  }

  // getSelectedFilterLabel(): string {
  //   return (
  //     this.filterOptions.find(f => f.value === this.filterStatus)?.label ||
  //     'Alle'
  //   );
  // }

  openDetail(reading: ReportMeterReading): void {
    console.log(reading);
    // later navigate to detail page
    // this.router.navigate(['/open-provider-action/report-meter-reading', reading.id]);
  }

  openSidebar(reading: ReportMeterReading): void {
    if (
      this.isSidebarOpen &&
      this.selectedReading?.id === reading.id
    ) {
      this.closeSidebar();
      return;
    }
    this.selectedReading = reading;
    this.isSidebarOpen = true;
  }

  closeSidebar(): void {
    this.selectedReading = null;
    this.isSidebarOpen = false;
  }

  customerInitial(name?: string): string {
    return name?.charAt(0)?.toUpperCase() || 'G';
  }

  openBookingDetails(deliveryId: number): void {
    if (!deliveryId) {
      return;
    }
    window.open(`/bookings/${deliveryId}`, '_blank');
  }
  
  getBookingStatus(reading: any): string {
    if (reading.isExpired === true) {
      return "Expired";
    }
    if (reading.signedFileUrl) {
      return "Document Uploaded";
    }
    if (reading.adminPlacedOrder === true) {
      return "Order Created";
    }
    if (reading.bookingOrderId == null) {
      return "Open Order";
    }
    return "Pending";
  }

  getImageUrl(path?: string): string {
    if (!path) return '';
    return this.IMAGE_BASE_URL + path;
  }

  openImageModal(): void {
    this.isImageModalOpen = true;
  }

  closeImageModal(): void {
    this.isImageModalOpen = false;
  }

  applyCategoryFilter(): void {
    if (!this.selectedCategory) {
      this.filteredReportMeterReadings =
        this.reportMeterReadings;
      return;
    }
    this.filteredReportMeterReadings =
      this.reportMeterReadings.filter(
        x => x.category === this.selectedCategory
      );
  }

  selectCategory(category: string): void {
    this.selectedCategory = category;
    this.isFilterOpen = false;
    this.applyCategoryFilter();
  }

}
