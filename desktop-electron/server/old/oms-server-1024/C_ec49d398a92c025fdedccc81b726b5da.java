import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_ec49d398a92c025fdedccc81b726b5da
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:window.open('module/bussines/zwdd/index_eventlog_yk_edit_view.html?dataname="+mapRow.get("TABLE_NAME")+"&&actiontype=edit&pk_id="+mapRow.get("ID")+"');";
return url;

    }
}
