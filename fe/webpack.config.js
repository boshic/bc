const packageJSON = require('./package.json');
const path = require('path');
const webpack = require('webpack');

const TerserJSPlugin = require('terser-webpack-plugin');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const OptimizeCSSAssetsPlugin = require('optimize-css-assets-webpack-plugin');
const NODE_ENV = process.env.NODE_ENV || 'development';

const PATHS = {
  build: path.join(__dirname, 'target', 'classes', 'META-INF', 'resources', 'webjars', packageJSON.name, packageJSON.version)
};

// console.log(path.join(__dirname, packageJSON.name));
module.exports = {
    // context: __dirname,
    entry: {
        main:'./app/index.js',
        prices: './app/modules/reports/common/prices.js',
        invoice: './app/modules/reports/common/invoice.js',
        writeOffAct: './app/modules/reports/common/writeOffAct.js'
    },

    output: {
        path: PATHS.build,
        publicPath: '../',
        filename: '[name].js'
    },
    // watch: NODE_ENV == 'development',
    // watchOptions : {
    //     aggregateTimeout: 100
    // },
   // mode: 'production',
   //  mode: 'development',
    mode: NODE_ENV == 'development' ? 'development' : 'production',
    devtool: NODE_ENV == 'development' ? 'source-map' : false,
    module: {
        rules: [
            {
                test: /\.js/,
                exclude: /node_modules/,
                use: {
                    loader: 'babel-loader',
                    options: {
                        presets: ['@babel/preset-env'],
                        plugins: ["angularjs-annotate"]
                    }
                }
            },
            {
                test: /\.(woff(2)?|ttf|eot|svg)(\?v=[0-9]\.[0-9]\.[0-9])?$/,
                loader: 'file-loader',
                options: {
                    limit: 30000,
                    name:'./css/fonts/[name].[ext]'
                },
            },
            {
                test: /\.mp3$/,
                loader: 'url-loader',
                options: {
                    limit: 30000,
                    name:'./media/audio/[name].[ext]'
                }
            },
            {
                // test: /\.css$/,
                // use: [MiniCssExtractPlugin.loader, 'css-loader'],
                test: /\.css$/,
                use: [
                    {
                        loader: MiniCssExtractPlugin.loader,
                        options: {
                            // you can specify a publicPath here
                            // by default it uses publicPath in webpackOptions.output
                            hmr: false,
                        },
                    },
                    'css-loader',
                ],
            },
            // {
            //     test: /\.css$/,
            //     include: [/styles\.css/],
            //     use: extractCSS.extract({
            //         fallback: 'style-loader',
            //         use: [{
            //             loader: 'css-loader'
            //         }]
            //     })
            // },
            // {
            //     test: /\.css$/,
            //     include: [/bootstrap\.css/],
            //     use: extractBSCSS.extract({
            //         fallback: 'style-loader',
            //         use: [{ loader: 'css-loader' }]
            //     })
            // },
            {
                test: /\.html$/,
                loader: "ng-cache-loader?prefix=[dir]/[dir]"
            }
        ],
        noParse: [/angular\/angular.js/, /bootstrap\/bootstrap.js/]
    },
    optimization: {
        minimizer: [new TerserJSPlugin({}), new OptimizeCSSAssetsPlugin({})],
    },
    plugins: [
        new MiniCssExtractPlugin({
            // Options similar to the same options in webpackOptions.output
            // both options are optional
            filename: '/css/[name].css',
            chunkFilename: '/css/[id].css',
        }),
        new webpack.ProvidePlugin({
            $: "jquery",
            jQuery: "jquery",
            "window.jQuery": "jquery"
        })
    ]
};
