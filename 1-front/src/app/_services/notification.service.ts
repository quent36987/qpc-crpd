import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface Notification {
  id: string;
  type: 'success' | 'error' | 'info' | 'warning';
  message: string;
  timestamp: Date;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private notificationsSubject = new BehaviorSubject<Notification[]>([]);
  public notifications$ = this.notificationsSubject.asObservable();

  private maxNotifications = 3;
  private autoHideDelay = 3000;

  constructor() {}

  private addNotification(type: 'success' | 'error' | 'info' | 'warning', message: string) {
    const notification: Notification = {
      id: this.generateId(),
      type,
      message,
      timestamp: new Date()
    };

    const currentNotifications = this.notificationsSubject.value;
    let updatedNotifications = [notification, ...currentNotifications];

    // Limiter à 3 notifications maximum
    if (updatedNotifications.length > this.maxNotifications) {
      updatedNotifications = updatedNotifications.slice(0, this.maxNotifications);
    }

    this.notificationsSubject.next(updatedNotifications);

    // Auto-hide après 3 secondes
    setTimeout(() => {
      this.removeNotification(notification.id);
    }, this.autoHideDelay);
  }

  success(message: string) {
    this.addNotification('success', message);
  }

  error(message: string) {
    this.addNotification('error', message);
  }

  info(message: string) {
    this.addNotification('info', message);
  }

  warning(message: string) {
    this.addNotification('warning', message);
  }

  removeNotification(id: string) {
    const currentNotifications = this.notificationsSubject.value;
    const updatedNotifications = currentNotifications.filter(n => n.id !== id);
    this.notificationsSubject.next(updatedNotifications);
  }

  clearAll() {
    this.notificationsSubject.next([]);
  }

  private generateId(): string {
    return Date.now().toString(36) + Math.random().toString(36).substr(2);
  }
}
