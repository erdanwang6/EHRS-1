import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  hide = true;
  submitting = false;
  loginError = '';

  openSnackBar(message: string, action: string) {
    this.snackBar.open(message, action, { duration: 4000 });
  }

  loginForm = this.formBuilder.group({
    username: ['', Validators.required],
    password: ['', Validators.required]
  });

  constructor(
    private snackBar: MatSnackBar,
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
  }

  onSubmit(): void {
    if (this.loginForm.invalid || this.submitting) {
      this.loginForm.markAllAsTouched();
      return;
    }

    const username = this.loginForm.get('username')?.value || '';
    const password = this.loginForm.get('password')?.value || '';
    const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl');

    this.submitting = true;
    this.loginError = '';

    this.authService.login(username, password)
      .pipe(finalize(() => {
        this.submitting = false;
      }))
      .subscribe({
        next: response => {
          this.openSnackBar(`Welcome back, ${response.user.displayName}.`, 'Dismiss');
          this.loginForm.reset();
          this.hide = true;
          this.router.navigateByUrl(returnUrl || response.user.homeRoute);
        },
        error: error => {
          this.loginError = error?.error?.message || 'Login failed. Please check your username and password.';
          this.openSnackBar(this.loginError, 'Dismiss');
          this.loginForm.patchValue({ password: '' });
        }
      });
  }

}
