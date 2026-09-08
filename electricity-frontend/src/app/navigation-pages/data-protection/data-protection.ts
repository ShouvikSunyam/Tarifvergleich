import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StaticContentService } from '../../services/static-content.service';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-data-protection',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './data-protection.html',
  styleUrls: ['./data-protection.css']
})
export class DataProtection implements OnInit {

  content: any;

  constructor(
    public staticContentService: StaticContentService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {

    this.staticContentService
      .getData()
      .subscribe({

        next: (res: any) => {

          console.log('API RESPONSE', res);

          this.content = res.find(
            (item: any) => item.id === 1
          );
          console.log("FILTERED CONTENT", this.content);
          this.cdr.detectChanges();
        },

        error: (err) => {

          console.log(err);
        }
      });
  }
}