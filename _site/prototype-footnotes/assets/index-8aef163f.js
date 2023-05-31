import{d as w,i as r,e as M,a as o,c as i,l as h,b,F as B,r as A,f as a,g as D,w as N,h as S,t as m,k as y,N as V,O as K,A as P,T as Y,u as F}from"./index-086108b4.js";import{d as U}from"./dayjs.min-633259b2.js";import{_ as j}from"./PopupModal.vue_vue_type_script_setup_true_lang-57b88188.js";import{d as k}from"./documentUnitService-4c93048b.js";import{_ as L}from"./Pagination.vue_vue_type_script_setup_true_lang-3db170aa.js";import"./documentUnit-2521476f.js";const R={key:1,class:"border-collapse document-unit-list-table table w-full"},z=K('<div class="bg-gray-400 font-bold leading-[3] table-row text-18 text-center"><div class="table-cell">Dokumentnummer</div><div class="table-cell">Angelegt am</div><div class="table-cell">Aktenzeichen</div><div class="table-cell">Dokumente</div><div class="table-cell">Löschen</div></div>',1),H={class:"px-[16px] py-0 table-cell"},O={class:"px-[16px] py-0 table-cell"},q={class:"px-[16px] py-0 table-cell"},E={class:"px-16 py-0 table-cell"},G={class:"table-cell text-center"},I=["onClick","onKeyup"],J={key:2},Q=w({__name:"DocumentUnitList",props:{documentUnitListEntries:null},emits:["deleteDocumentUnit"],setup(p,{emit:v}){const n=r(!1),l=r(""),f=r("Löschen"),u="Dokumentationseinheit löschen",_="ghost",s="secondary",e=r(),c=()=>{if(n.value=!n.value,n.value){const d=document.documentElement.scrollLeft,x=document.documentElement.scrollTop;window.onscroll=()=>{window.scrollTo(d,x)}}else window.onscroll=()=>{}},g=d=>{e.value=d,l.value=`Möchten Sie die Dokumentationseinheit ${e.value.documentNumber} wirklich dauerhaft löschen?`,c()},C=()=>{e.value&&(v("deleteDocumentUnit",e.value),c())};return(d,x)=>{const T=M("router-link");return o(),i("div",null,[n.value?(o(),h(j,{key:0,"aria-label":u,"cancel-button-type":_,"confirm-button-type":s,"confirm-text":f.value,"content-text":l.value,"header-text":u,onCloseModal:c,onConfirmAction:C},null,8,["confirm-text","content-text"])):b("",!0),p.documentUnitListEntries.length?(o(),i("div",R,[z,(o(!0),i(B,null,A(p.documentUnitListEntries,t=>(o(),i("div",{key:t.id,class:"border-b-2 border-b-gray-100 hover:bg-gray-100 leading-[3] table-row text-18"},[a("div",H,[D(T,{class:"underline",to:{name:t.fileName?"caselaw-documentUnit-:documentNumber-categories":"caselaw-documentUnit-:documentNumber-files",params:{documentNumber:t.documentNumber}}},{default:N(()=>[S(m(t.documentNumber),1)]),_:2},1032,["to"])]),a("div",O,m(y(U)(t.creationTimestamp).format("DD.MM.YYYY")),1),a("div",q,m(t&&t.fileNumber?t.fileNumber:"-"),1),a("div",E,m(t.fileName?t.fileName:"-"),1),a("div",G,[a("span",{"aria-label":"Dokumentationseinheit löschen",class:"cursor-pointer material-icons",tabindex:"0",onClick:$=>g(t),onKeyup:V($=>g(t),["enter"])}," delete ",40,I)])]))),128))])):(o(),i("span",J,"Keine Dokumentationseinheiten gefunden"))])}}}),W={class:"flex flex-col gap-16 p-16"},X={class:"flex justify-between"},Z=a("h1",{class:"heading-02-regular"},"Übersicht Rechtsprechung",-1),le=w({__name:"index",setup(p){const v=F(),n=r(),l=r(),f=30;async function u(s){const e=await k.getAllListEntries(s,f);e.data?(n.value=e.data.content,l.value=e.data):console.error("could not load list entries")}async function _(s){if(n.value){const e=await k.delete(s.uuid);e.status===200?n.value=n.value.filter(c=>c!=s):alert("Fehler beim Löschen der Dokumentationseinheit: "+e.data)}}return P(async()=>{await u(0)}),(s,e)=>(o(),i("div",W,[a("div",X,[Z,D(Y,{label:"Neue Dokumentationseinheit",onClick:e[0]||(e[0]=c=>y(v).push({name:"caselaw-documentUnit-new"}))})]),l.value?(o(),h(L,{key:0,"navigation-position":"bottom",page:l.value,onUpdatePage:u},{default:N(()=>[n.value?(o(),h(Q,{key:0,class:"grow","document-unit-list-entries":n.value,onDeleteDocumentUnit:_},null,8,["document-unit-list-entries"])):b("",!0)]),_:1},8,["page"])):b("",!0)]))}});export{le as default};