import { Component, OnInit, OnDestroy } from "@angular/core";
import { CommonModule, NgClass } from "@angular/common";
import { ApiService } from "../../../shared/services/api.service";
import { Router } from "@angular/router";
import { HttpClient } from "@angular/common/http";
import { FormsModule } from "@angular/forms";
import { Subject, Subscription } from "rxjs";
import { debounceTime, distinctUntilChanged } from "rxjs/operators";

export interface CustomerInvoiceRequest {
  id?: number;
  salutation?: string;
  customerName?: string;
  customerEmail?: string;
  message?: string;
  deliveryId?: any;
  // deliveryType?: string;
  orderId?: number;
  connectionId?: number;
  invoiceCategory?: string;
  status?: number;
  createdAt?: string;
  bookingId?: number;
  bookingStatus?: string;
  bookingCreatedOn?: number;
}

@Component({
  selector: "app-customer-invoice-request",
  imports: [CommonModule, NgClass, FormsModule],
  standalone: true,
  templateUrl: "./customer-invoice-request.component.html",
  styleUrl: "./customer-invoice-request.component.css",
})
export class CustomerInvoiceRequestComponent implements OnInit, OnDestroy {
  customerInvoiceRequests: CustomerInvoiceRequest[] = [];

  isLoading = false;
  errorMessage = "";

  selectedRequest: any = null;
  isSidebarOpen = false;

  filterStatus = 0;
  filterOptions = [
    { value: 0, label: "Alle" },
    { value: 1, label: "Aktiv" },
    { value: 2, label: "Inaktiv" },
  ];
  isFilterOpen = false;

  searchTerm = "";
  private searchTerm$ = new Subject<string>();
  private searchSub!: Subscription;

  constructor(
    // private api: ApiService,
    private http: HttpClient,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.searchSub = this.searchTerm$
      .pipe(debounceTime(350), distinctUntilChanged())
      .subscribe(() => {
        this.fetchCustomerInvoiceRequests();
      });

    this.fetchCustomerInvoiceRequests();
  }

  ngOnDestroy(): void {
    this.searchSub?.unsubscribe();
  }

  onSearchInput(value: string): void {
    this.searchTerm = value ?? "";
    this.searchTerm$.next(this.searchTerm);
  }

  clearSearch(): void {
    if (!this.searchTerm) {
      return;
    }
    this.searchTerm = "";
    this.searchTerm$.next("");
  }

  fetchCustomerInvoiceRequests(): void {
    this.isLoading = true;
    this.errorMessage = "";

    this.closeSidebar();
    const payload = {
      search: this.searchTerm?.trim() || "",
    };
    this.http
      .post("http://192.168.0.155:8080/admin/customer-invoice-request", payload)

      .subscribe({
        next: (res: any) => {
          this.isLoading = false;

          const data = Array.isArray(res)
            ? res
            : Array.isArray(res?.data)
              ? res.data
              : [];
          // this.customerInvoiceRequests = data;
          this.customerInvoiceRequests = data.sort(
            (a: CustomerInvoiceRequest, b: CustomerInvoiceRequest) =>
              (a.id ?? 0) - (b.id ?? 0)
          );
        },

        error: (err: any) => {
          this.isLoading = false;
          this.errorMessage = "Fehler beim Laden der Messwertmeldungen";

          console.error(err);
        },
      });
  }

  trackById(index: number, item: CustomerInvoiceRequest): number {
    return item.id ?? index;
  }

  formatDate(date?: string): string {
    if (!date) return "—";
    return new Intl.DateTimeFormat("de-DE", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
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
    return this.customerInvoiceRequests.filter((item) => item.status === 1)
      .length;
  }

  getSelectedFilterLabel(): string {
    return (
      this.filterOptions.find((f) => f.value === this.filterStatus)?.label ||
      "Alle"
    );
  }

  openDetail(request: CustomerInvoiceRequest): void {
    console.log(request);
  }

  selectedIndex: number | null = null;

  openSidebar(request: CustomerInvoiceRequest, index: number): void {
    if (this.isSidebarOpen && this.selectedRequest?.id === request.id) {
      this.closeSidebar();
      return;
    }
    this.selectedRequest = request;
    this.selectedIndex = index;
    this.isSidebarOpen = true;
  }
  closeSidebar(): void {
    this.selectedRequest = null;
    this.isSidebarOpen = false;
  }

  customerInitial(name?: string): string {
    return name?.charAt(0)?.toUpperCase() || "G";
  }

  openBookingDetails(deliveryId: number): void {
    if (!deliveryId) {
      return;
    }
    // window.open(`/bookings/${deliveryId}`, '_blank');
    window.location.href = `/bookings/${deliveryId}`;
  }

  getBookingStatus(request: any): string {
    if (request.isExpired === true) {
      return "Expired";
    }
    if (request.signedFileUrl) {
      return "Document Uploaded";
    }
    if (request.adminPlacedOrder === true) {
      return "Order Created";
    }
    if (request.bookingOrderId == null) {
      return "Open Order";
    }
    return "Pending";
  }
}
