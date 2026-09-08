import { HttpClient } from "@angular/common/http";
import { Component, OnInit, OnDestroy } from "@angular/core";
import { CommonModule, NgClass } from "@angular/common";
import { ApiService } from "../../../shared/services/api.service";
import { Router } from "@angular/router";

import { FormsModule } from "@angular/forms";
import { Subject, Subscription } from "rxjs";
import { debounceTime, distinctUntilChanged } from "rxjs/operators";
export interface CustomerSupplierMessage {
  id?: number;
  salutation?: string;
  customerName?: string;
  customerEmail?: string;
  message?: string;
  deliveryId?: { deliveryId?: number; uniqueDeliveryId?: string };
  invoiceCategory?: string;
  status?: number;
  statusLabel: string;
  createdAt?: number;
  bookingCreatedOn?: number;
  isExpired?: boolean;
  signedFileUrl?: string;
  adminPlacedOrder?: boolean;
  bookingOrderId?: number;
}

@Component({
  selector: "app-energy-supplier",
  imports: [CommonModule, NgClass, FormsModule],
  templateUrl: "./energy-supplier.component.html",
  styleUrl: "./energy-supplier.component.css",
})
export class EnergySupplierComponent {
  customerSupplierMessage: CustomerSupplierMessage[] = [];

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
        this.fetchCustomerSupplierMessage();
      });

    this.fetchCustomerSupplierMessage();
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
  
  fetchCustomerSupplierMessage(): void {
    this.isLoading = true;
    this.errorMessage = "";
    this.closeSidebar();

    const payload = {
      search: this.searchTerm?.trim() || "",
      adminId: 1,
      page: 1,
    };

    this.http
      .post(
        "http://192.168.0.155:8080/admin/fetch-all-supplier-message",
        payload,
      )
      .subscribe({
        next: (res: any) => {
          this.isLoading = false;

          const items = Array.isArray(res?.data) ? res.data : [];

          this.customerSupplierMessage = items.map((item: any) => {
            const detail = item.customerOrderDetail;
            const order = detail?.order;

            return {
              id: item.supplierMesageId,
              salutation: detail?.title,
              customerName:
                `${detail?.firstName ?? ""} ${detail?.lastName ?? ""}`.trim(),
              customerEmail: detail?.email,
              message: item.message,
              invoiceCategory: item.categoryName,
              status: item.status,
              statusLabel: item.statusLabel,
              createdAt: item.addedOn,
              deliveryId: {
                deliveryId: detail?.deliveryId,
                uniqueDeliveryId: detail?.uniqueDeliveryId,
              },
              bookingCreatedOn: order?.adminOrderPlacedOn,
              isExpired: order?.isExpired,
              signedFileUrl: order?.doc?.signedFileUrl,
              adminPlacedOrder: order?.adminPlacedOrder,
              bookingOrderId: order?.orderId,
            };
          });
        },

        error: (err: any) => {
          this.isLoading = false;
          this.errorMessage = "Fehler beim Laden der Messwertmeldungen";
          console.error(err);
        },
      });
  }

  trackById(index: number, item: CustomerSupplierMessage): number {
    return item.id ?? index;
  }

  formatDate(timestamp?: number | string): string {
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

    return new Intl.DateTimeFormat("de-DE", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    }).format(date);
  }

  getActiveCount(): number {
    return this.customerSupplierMessage.filter((item) => item.status === 1)
      .length;
  }

  getSelectedFilterLabel(): string {
    return (
      this.filterOptions.find((f) => f.value === this.filterStatus)?.label ||
      "Alle"
    );
  }

  openDetail(request: CustomerSupplierMessage): void {
    console.log(request);
  }

  selectedIndex: number | null = null;

  openSidebar(request: CustomerSupplierMessage, index: number): void {
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
    this.selectedIndex = null;
    this.isSidebarOpen = false;
  }

  customerInitial(name?: string): string {
    return name?.charAt(0)?.toUpperCase() || "G";
  }

  openBookingDetails(deliveryId: number): void {
    if (!deliveryId) {
      return;
    }
    // window.open(`/bookings/${deliveryId}`, "_blank");
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
