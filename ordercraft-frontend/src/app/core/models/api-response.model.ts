export interface ApiResponse<T> {
  data?: T;
  message?: string;
  error?: string;
  details?: string[];
  timestamp?: string;
  page?: number;
  size?: number;
  totalElements?: number;
  totalPages?: number;
}
