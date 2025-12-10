import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Subscription } from 'rxjs';
import {NotificationService, Notification} from "../../../_services/notification.service";


@Component({
  selector: 'app-notification',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notifications.html',
  styleUrl: './notifications.scss'
})
export class NotificationsComponent implements OnInit, OnDestroy {
  notifications: Notification[] = [];
  private subscription: Subscription = new Subscription();

  constructor(private notificationService: NotificationService) {}

  ngOnInit() {
    this.subscription = this.notificationService.notifications$.subscribe(
      notifications => {
        this.notifications = notifications;
      }
    );
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  removeNotification(id: string) {

    const notificationElement = document.querySelector(`[data-notification-id="${id}"]`);
    if (notificationElement) {
      notificationElement.classList.add('removing');
      setTimeout(() => {
        this.notificationService.removeNotification(id);
      }, 300);
    } else {
      this.notificationService.removeNotification(id);
    }
  }

  getIcon(type: string): string {
    const icons: { [key: string]: string } = {
      'success': 'check_circle',
      'error': 'error',
      'info': 'info',
      'warning': 'warning'
    };
    return icons[type] || 'info';
  }

  trackByFn(index: number, item: Notification): string {
    return item.id;
  }
}
