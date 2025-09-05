#include <stdlib.h>
#include "rashunal.h"
#include "rmatrix.h"
#include "org_jtodd_jni_RMatrixJNI.h"

JNIEXPORT jobject JNICALL Java_org_jtodd_jni_RMatrixJNI_factor (JNIEnv *env, jobject thisObject, jobjectArray jdata)
{
    long height = (long)(*env)->GetArrayLength(env, jdata);
    jarray first_row = (*env)->GetObjectArrayElement(env, jdata, 0);
    long width = (long)(*env)->GetArrayLength(env, first_row);

    size_t total = height * width;
    Rashunal **data = malloc(sizeof(Rashunal *) * total);
    for (size_t i = 0; i < total; ++i) {
        size_t row_index = i / width;
        size_t col_index = i % width;
        jarray row = (*env)->GetObjectArrayElement(env, jdata, row_index);
        jarray jel = (*env)->GetObjectArrayElement(env, row, col_index);
        long el_count = (long)(*env)->GetArrayLength(env, jel);
        jint *el = (*env)->GetIntArrayElements(env, jel, JNI_FALSE);
        int numerator = (int)el[0];
        int denominator = el_count == 1 ? 1 : (int)el[1];
        data[i] = n_Rashunal(numerator, denominator);
    }
    RMatrix *m = new_RMatrix(height, width, data);
    Gauss_Factorization *f = RMatrix_gelim(m);
    const RMatrix *u = f->u;

    size_t u_height = RMatrix_height(u);
    size_t u_width = RMatrix_width(u);
    jclass j_rashunal_class = (*env)->FindClass(env, "org/jtodd/jni/JRashunal");
    jclass j_rmatrix_class = (*env)->FindClass(env, "org/jtodd/jni/JRashunalMatrix");
    jmethodID j_rashunal_constructor = (*env)->GetMethodID(env, j_rashunal_class, "<init>", "(II)V");
    jmethodID j_rmatrix_constructor = (*env)->GetMethodID(env, j_rmatrix_class, "<init>", "(II[Lorg/jtodd/jni/JRashunal;)V");
    jobjectArray j_rashunal_data = (*env)->NewObjectArray(env, u_height * u_width, j_rashunal_class, NULL);
    for (size_t i = 0; i < total; ++i) {
        const Rashunal *r = RMatrix_get(u, i / width + 1, i % width + 1);
        jobject j_rashunal = (*env)->NewObject(env, j_rashunal_class, j_rashunal_constructor, r->numerator, r->denominator);
        (*env)->SetObjectArrayElement(env, j_rashunal_data, i, j_rashunal);
        free((Rashunal *)r);
    }
    jobject j_rmatrix = (*env)->NewObject(env, j_rmatrix_class, j_rmatrix_constructor, RMatrix_height(u), RMatrix_width(u), j_rashunal_data);
    
    free(data);
    free_RMatrix((RMatrix *)m);
    free_RMatrix((RMatrix *)f->pi);
    free_RMatrix((RMatrix *)f->l);
    free_RMatrix((RMatrix *)f->d);
    free_RMatrix((RMatrix *)f->u);
    free(f);

    return j_rmatrix;
}
