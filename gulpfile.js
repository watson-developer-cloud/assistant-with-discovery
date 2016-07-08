(function () {
    var gulp = require ( 'gulp' );
    var $ = require ( 'gulp-load-plugins' ) ();
    var tslint = require('gulp-tslint');

    var appDev = './src/main/webapp/';
    var appProd = './src/main/webapp/dist/';

    /* Mixed */
    var ext_replace = require ( 'gulp-ext-replace' );

    /* CSS */
    var postcss = require ( 'gulp-postcss' );
    var sourcemaps = require ( 'gulp-sourcemaps' );
    var autoprefixer = require ( 'autoprefixer' );
    var precss = require ( 'precss' );
    var cssnano = require ( 'cssnano' );

    /* JS & TS */
    var typescript = require ( 'gulp-typescript' );

    /* Images */
    var imagemin = require ( 'gulp-imagemin' );

    var tsProject = typescript.createProject ( 'src/main/webapp/ts/tsconfig.json' );

    gulp.task ( 'build-css', function () {
        return gulp.src ( appDev + 'scss/*.scss' )
            .pipe ( sourcemaps.init () )
            .pipe ( postcss ( [precss, autoprefixer, cssnano] ) )
            .pipe ( sourcemaps.write ('.') )
            .pipe ( ext_replace ( '.css' ) )
            .pipe ( gulp.dest ( appProd + 'css/' ) )
            .pipe ( $.size ( {'title': 'css'} ) );
    } );

    gulp.task ( 'build-ts', function () {
        var result = gulp.src ( [appDev + 'ts/**/*.ts'] )
            .pipe(tslint())
        	  .pipe(tslint.report("verbose"))
            .pipe ( sourcemaps.init () )
            .pipe(typescript(tsProject));

        result.dts.pipe(gulp.dest(appProd + 'js')); //pipe the js files to dist folder

        return result.js.pipe (sourcemaps.write ('.'))
            .pipe ( gulp.dest ( appProd + 'js' ) ) //pipe the source maps
            .pipe ( $.size ( {'title': 'ts'} ) );
    } );

    gulp.task ( 'build', ['build-ts', 'build-css','build-img','build-fonts','build-locale'], function () {
        return gulp.src ( appDev + 'index.html' )
            .pipe ( $.useref ( {'searchPath': './'} ) ) //node_modules dir is in the current dir, search there for dependencies!
            .pipe ( gulp.dest ( appProd ) )
            .pipe ( $.size ( {'title': 'html'} ) );
    } );

    gulp.task ( 'build-img', function () {
        return gulp.src ( appDev + 'img/**/*' )
            .pipe ( imagemin ( {
                progressive: true
            } ) )
            .pipe ( gulp.dest ( appProd + 'img/' ) );
    } );

    gulp.task('build-fonts', function() {
        return gulp.src(appDev + 'fonts/**/*')
            .pipe(gulp.dest ( appProd + 'fonts/' ));
    });

    gulp.task('build-locale', function() {
        return gulp.src(appDev + 'locale/**/*')
            .pipe(gulp.dest ( appProd + 'locale/' ));
    });

    gulp.task('clean', function(){
        return gulp.src(appProd, {read: false})
            .pipe($.clean());
    });

    gulp.task ( 'watch', function () {
        gulp.watch ( appDev + '**/*.ts', ['build-ts'] );
        gulp.watch ( appDev + 'scss/**/*.scss', ['build-css'] );
        gulp.watch ( appDev + 'img/*', ['build-img'] );
    } );

    gulp.task ( 'default', ['build-html'] );
} ())
