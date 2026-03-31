import { Component, OnInit } from '@angular/core';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  constructor(private authService: AuthService) { }

  ngOnInit(): void {
  }

  get displayName(): string {
    return this.authService.getCurrentUser()?.displayName || 'Guest';
  }

  get primaryRole(): string {
    return this.authService.getCurrentUser()?.primaryRole || '';
  }

  onLogout(): void {
    this.authService.logout();
  }

}
