package com.wisdom.common.activiti.exception;


public class RollbackException extends RuntimeException{


    		private static final long serialVersionUID = 1L;

		public RollbackException() {
			super();
			// TODO Auto-generated constructor stub
		}

		public RollbackException(String message, Throwable cause,
				boolean enableSuppression, boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
			// TODO Auto-generated constructor stub
		}

		public RollbackException(String message, Throwable cause) {
			super(message, cause);
			// TODO Auto-generated constructor stub
		}

		public RollbackException(String message) {
			super(message);
			// TODO Auto-generated constructor stub
		}

		public RollbackException(Throwable cause) {
			super(cause);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void printStackTrace() {
			
		}	
		
		
	}

