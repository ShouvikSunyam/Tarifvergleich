import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, shareReplay, map } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class StaticContentService {

  private readonly API_URL = 'http://192.168.0.155:8080/api/static-content/all';
  private readonly BASE_IMAGE_URL = 'http://192.168.0.155:8080/assets/super-admin/';

  private data$: Observable<any>;

  constructor(private http: HttpClient) {
    this.data$ = this.http.post<any>(this.API_URL, {}).pipe( shareReplay(1) );
  }

  // GET ALL DATA
  getData(): Observable<any> {
    return this.data$;
  }

  // GET CONTENT BY ID
  getContentById(id: number): Observable<any> {
    return this.data$.pipe( map((res: any[]) => res.find((item: any) => item.id === id) )
    );
  }

  // IMAGE URL
  getImageUrl(contentUrl: string | null): string {
    if (!contentUrl) return '';
    console.log(this.BASE_IMAGE_URL + contentUrl);
    
    return `http://192.168.0.155:8080/assets/super-admin/${contentUrl}`;
  }
}