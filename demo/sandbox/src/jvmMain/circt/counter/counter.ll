; ModuleID = 'LLVMDialectModule'
source_filename = "LLVMDialectModule"

declare void @exit(i32)

define void @Counter_eval(ptr %0) {
  %2 = load i1, ptr %0, align 1
  %3 = getelementptr i8, ptr %0, i32 2
  store i1 %2, ptr %3, align 1
  %4 = getelementptr i8, ptr %0, i32 1
  %5 = load i1, ptr %4, align 1
  %6 = getelementptr i8, ptr %0, i32 3
  store i1 %5, ptr %6, align 1
  %7 = load i1, ptr %0, align 1
  %8 = getelementptr i8, ptr %0, i32 4
  %9 = load i1, ptr %8, align 1
  store i1 %7, ptr %8, align 1
  %10 = xor i1 %9, %7
  %11 = and i1 %10, %7
  br i1 %11, label %12, label %21

12:                                               ; preds = %1
  %13 = load i1, ptr %4, align 1
  br i1 %13, label %14, label %16

14:                                               ; preds = %12
  %15 = getelementptr i8, ptr %0, i32 5
  store i8 0, ptr %15, align 1
  br label %20

16:                                               ; preds = %12
  %17 = getelementptr i8, ptr %0, i32 5
  %18 = load i8, ptr %17, align 1
  %19 = add i8 %18, 1
  store i8 %19, ptr %17, align 1
  br label %20

20:                                               ; preds = %14, %16
  br label %21

21:                                               ; preds = %20, %1
  %22 = getelementptr i8, ptr %0, i32 5
  %23 = load i8, ptr %22, align 1
  %24 = getelementptr i8, ptr %0, i32 6
  store i8 %23, ptr %24, align 1
  %25 = load i8, ptr %22, align 1
  %26 = getelementptr i8, ptr %0, i32 7
  store i8 %25, ptr %26, align 1
  ret void
}

!llvm.module.flags = !{!0}

!0 = !{i32 2, !"Debug Info Version", i32 3}
