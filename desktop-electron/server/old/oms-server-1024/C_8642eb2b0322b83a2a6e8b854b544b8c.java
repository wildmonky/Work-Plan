import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_8642eb2b0322b83a2a6e8b854b544b8c
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        String url="javascript:jsCommon.deleteTableData('W_ASFPGL','"+mapRow.get("ID")+"');";
return url;

    }
}
