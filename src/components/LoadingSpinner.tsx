// src/components/LoadingSpinner.tsx
interface LoadingSpinnerProps {
    size?: 'sm' | 'md' | 'lg';
    message?: string;
}

const LoadingSpinner = ({ size = 'md', message }: LoadingSpinnerProps) => {
    const sizeClasses = {
        sm: 'h-6 w-6',
        md: 'h-12 w-12',
        lg: 'h-16 w-16'
    };

    return (
        <div className="flex flex-col items-center justify-center py-8">
            <div className={`animate-spin rounded-full border-b-2 border-green-600 ${sizeClasses[size]} mb-4`}></div>
            {message && (
                <p className="text-gray-600 text-center">{message}</p>
            )}
        </div>
    );
};

export default LoadingSpinner;